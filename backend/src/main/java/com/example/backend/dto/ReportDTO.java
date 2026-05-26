package com.example.backend.dto;

import com.example.backend.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    
    private Long id;
    private Long reporterId;
    private String reporterName;
    private String reporterAvatar;
    private Long productId;
    private String productTitle;
    private String productImage;
    private Report.ReportType type;
    private String reason;
    private Report.ReportStatus status;
    private String handleResult;
    private Long handlerId;
    private String handlerName;
    private LocalDateTime handleTime;
    private LocalDateTime createTime;
    
    public static ReportDTO fromEntity(Report report) {
        String productImage = null;
        if (report.getProduct() != null && report.getProduct().getImages() != null 
                && !report.getProduct().getImages().isEmpty()) {
            productImage = report.getProduct().getImages().get(0).getImageUrl();
        }
        
        return ReportDTO.builder()
                .id(report.getId())
                .reporterId(report.getReporter() != null ? report.getReporter().getId() : null)
                .reporterName(report.getReporter() != null ? report.getReporter().getUsername() : null)
                .reporterAvatar(report.getReporter() != null ? report.getReporter().getAvatar() : null)
                .productId(report.getProduct() != null ? report.getProduct().getId() : null)
                .productTitle(report.getProduct() != null ? report.getProduct().getTitle() : null)
                .productImage(productImage)
                .type(report.getType())
                .reason(report.getReason())
                .status(report.getStatus())
                .handleResult(report.getHandleResult())
                .handlerId(report.getHandler() != null ? report.getHandler().getId() : null)
                .handlerName(report.getHandler() != null ? report.getHandler().getUsername() : null)
                .handleTime(report.getHandleTime())
                .createTime(report.getCreateTime())
                .build();
    }
}
