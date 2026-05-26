package com.example.backend.service;

import com.example.backend.dto.ReportDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Notification;
import com.example.backend.entity.Product;
import com.example.backend.entity.Report;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.NotificationRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.ReportRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public PageResponse<ReportDTO> getMyReports(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Report> reportPage = reportRepository.findByReporterIdWithRelations(userId, pageable);
        Page<ReportDTO> dtoPage = reportPage.map(ReportDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public PageResponse<ReportDTO> getReports(Report.ReportStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Report> reportPage;
        if (status != null) {
            reportPage = reportRepository.findByStatusWithRelations(status, pageable);
        } else {
            reportPage = reportRepository.findAllWithRelations(pageable);
        }
        Page<ReportDTO> dtoPage = reportPage.map(ReportDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public ReportDTO createReport(Long reporterId, Long productId, Report.ReportType type, String reason) {
        if (reportRepository.existsByReporterIdAndProductId(reporterId, productId)) {
            throw new BusinessException("您已举报过该商品");
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", reporterId));
        Product product = productRepository.findByIdWithSellerAndImages(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Report report = new Report();
        report.setReporter(reporter);
        report.setProduct(product);
        report.setType(type);
        report.setReason(reason);
        report.setStatus(Report.ReportStatus.PENDING);

        Report saved = reportRepository.save(report);
        return ReportDTO.fromEntity(saved);
    }

    @Transactional
    public ReportDTO handleReport(Long id, Long handlerId, Report.ReportStatus status, String result) {
        Report report = reportRepository.findByIdWithRelations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report", "id", id));

        User handler = userRepository.findById(handlerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", handlerId));

        report.setStatus(status);
        report.setHandleResult(result);
        report.setHandler(handler);
        report.setHandleTime(LocalDateTime.now());

        if (status == Report.ReportStatus.RESOLVED) {
            Product product = report.getProduct();
            product.setStatus(Product.ProductStatus.OFF_SHELF);
            productRepository.save(product);
        }

        Notification notification = new Notification();
        notification.setUser(report.getReporter());
        notification.setTitle("举报处理结果");
        notification.setContent("您举报的商品「" + report.getProduct().getTitle() + "」已处理：" + result);
        notification.setType(Notification.NotificationType.SYSTEM);
        notification.setRelatedId(report.getId());
        notificationRepository.save(notification);

        Report saved = reportRepository.save(report);
        return ReportDTO.fromEntity(saved);
    }
}
