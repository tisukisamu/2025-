package com.example.backend.service;

import com.example.backend.dto.BuyRequestDTO;
import com.example.backend.dto.BuyRequestResponseDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.BuyRequest;
import com.example.backend.entity.BuyRequestResponse;
import com.example.backend.entity.Product;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BuyRequestRepository;
import com.example.backend.repository.BuyRequestResponseRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BuyRequestService {

    private final BuyRequestRepository buyRequestRepository;
    private final BuyRequestResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public PageResponse<BuyRequestDTO> getBuyRequests(BuyRequest.BuyRequestStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BuyRequest> requestPage;
        if (status != null) {
            requestPage = buyRequestRepository.findByStatusWithUser(status, pageable);
        } else {
            requestPage = buyRequestRepository.findAllWithUser(pageable);
        }
        Page<BuyRequestDTO> dtoPage = requestPage.map(BuyRequestDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public BuyRequestDTO getBuyRequestById(Long id) {
        BuyRequest request = buyRequestRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequest", "id", id));
        request.setViewCount(request.getViewCount() + 1);
        buyRequestRepository.save(request);
        return BuyRequestDTO.fromEntity(request);
    }

    public PageResponse<BuyRequestDTO> getMyBuyRequests(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BuyRequest> requestPage = buyRequestRepository.findByUserIdWithUser(userId, pageable);
        Page<BuyRequestDTO> dtoPage = requestPage.map(BuyRequestDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public BuyRequestDTO createBuyRequest(Long userId, String title, String description, String category,
                                          BigDecimal budgetMin, BigDecimal budgetMax, String expectedCondition,
                                          String contactInfo, Integer expireDays) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        BuyRequest request = new BuyRequest();
        request.setUser(user);
        request.setTitle(title);
        request.setDescription(description);
        request.setCategory(category);
        request.setBudgetMin(budgetMin);
        request.setBudgetMax(budgetMax);
        request.setExpectedCondition(expectedCondition);
        request.setContactInfo(contactInfo);
        request.setStatus(BuyRequest.BuyRequestStatus.OPEN);
        if (expireDays != null && expireDays > 0) {
            request.setExpireTime(LocalDateTime.now().plusDays(expireDays));
        }

        BuyRequest saved = buyRequestRepository.save(request);
        return BuyRequestDTO.fromEntity(saved);
    }

    @Transactional
    public BuyRequestDTO updateBuyRequest(Long id, Long userId, String title, String description, String category,
                                          BigDecimal budgetMin, BigDecimal budgetMax, String expectedCondition,
                                          String contactInfo) {
        BuyRequest request = buyRequestRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequest", "id", id));

        if (!request.getUser().getId().equals(userId)) {
            throw new BusinessException("无权修改此求购信息");
        }

        if (title != null) request.setTitle(title);
        if (description != null) request.setDescription(description);
        if (category != null) request.setCategory(category);
        if (budgetMin != null) request.setBudgetMin(budgetMin);
        if (budgetMax != null) request.setBudgetMax(budgetMax);
        if (expectedCondition != null) request.setExpectedCondition(expectedCondition);
        if (contactInfo != null) request.setContactInfo(contactInfo);

        BuyRequest saved = buyRequestRepository.save(request);
        return BuyRequestDTO.fromEntity(saved);
    }

    @Transactional
    public void closeBuyRequest(Long id, Long userId) {
        BuyRequest request = buyRequestRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequest", "id", id));

        if (!request.getUser().getId().equals(userId)) {
            throw new BusinessException("无权关闭此求购信息");
        }

        request.setStatus(BuyRequest.BuyRequestStatus.CLOSED);
        buyRequestRepository.save(request);
    }

    public PageResponse<BuyRequestResponseDTO> getResponses(Long buyRequestId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<BuyRequestResponse> responsePage = responseRepository.findByBuyRequestIdWithRelations(buyRequestId, pageable);
        Page<BuyRequestResponseDTO> dtoPage = responsePage.map(BuyRequestResponseDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public BuyRequestResponseDTO createResponse(Long buyRequestId, Long responderId, Long productId,
                                                String message, BigDecimal offeredPrice) {
        BuyRequest request = buyRequestRepository.findById(buyRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequest", "id", buyRequestId));

        if (request.getStatus() != BuyRequest.BuyRequestStatus.OPEN) {
            throw new BusinessException("该求购已关闭");
        }

        if (responseRepository.existsByBuyRequestIdAndResponderId(buyRequestId, responderId)) {
            throw new BusinessException("您已响应过此求购");
        }

        User responder = userRepository.findById(responderId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", responderId));

        BuyRequestResponse response = new BuyRequestResponse();
        response.setBuyRequest(request);
        response.setResponder(responder);
        response.setMessage(message);
        response.setOfferedPrice(offeredPrice);

        if (productId != null) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            response.setProduct(product);
        }

        BuyRequestResponse saved = responseRepository.save(response);
        request.setResponseCount(request.getResponseCount() + 1);
        buyRequestRepository.save(request);
        return BuyRequestResponseDTO.fromEntity(saved);
    }

    @Transactional
    public BuyRequestResponseDTO acceptResponse(Long responseId, Long userId) {
        BuyRequestResponse response = responseRepository.findByIdWithRelations(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequestResponse", "id", responseId));

        if (!response.getBuyRequest().getUser().getId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        response.setStatus(BuyRequestResponse.ResponseStatus.ACCEPTED);
        response.getBuyRequest().setStatus(BuyRequest.BuyRequestStatus.IN_PROGRESS);

        BuyRequestResponse saved = responseRepository.save(response);
        return BuyRequestResponseDTO.fromEntity(saved);
    }

    @Transactional
    public void rejectResponse(Long responseId, Long userId) {
        BuyRequestResponse response = responseRepository.findByIdWithRelations(responseId)
                .orElseThrow(() -> new ResourceNotFoundException("BuyRequestResponse", "id", responseId));

        if (!response.getBuyRequest().getUser().getId().equals(userId)) {
            throw new BusinessException("无权操作");
        }

        response.setStatus(BuyRequestResponse.ResponseStatus.REJECTED);
        responseRepository.save(response);
    }
}
