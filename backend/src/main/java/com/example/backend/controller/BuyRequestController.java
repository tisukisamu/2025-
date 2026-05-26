package com.example.backend.controller;

import com.example.backend.dto.BuyRequestDTO;
import com.example.backend.dto.BuyRequestResponseDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.BuyRequest;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.BuyRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/buy-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BuyRequestController {

    private final BuyRequestService buyRequestService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BuyRequestDTO>>> getBuyRequests(
            @RequestParam(required = false) BuyRequest.BuyRequestStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BuyRequestDTO> requests = buyRequestService.getBuyRequests(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BuyRequestDTO>> getBuyRequestById(@PathVariable Long id) {
        BuyRequestDTO request = buyRequestService.getBuyRequestById(id);
        return ResponseEntity.ok(ApiResponse.success(request));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<BuyRequestDTO>>> getMyBuyRequests(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BuyRequestDTO> requests = buyRequestService.getMyBuyRequests(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BuyRequestDTO>> createBuyRequest(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal budgetMin,
            @RequestParam(required = false) BigDecimal budgetMax,
            @RequestParam(required = false) String expectedCondition,
            @RequestParam(required = false) String contactInfo,
            @RequestParam(required = false) Integer expireDays) {
        BuyRequestDTO request = buyRequestService.createBuyRequest(
                userDetails.getId(), title, description, category, budgetMin, budgetMax,
                expectedCondition, contactInfo, expireDays);
        return ResponseEntity.ok(ApiResponse.success("发布成功", request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BuyRequestDTO>> updateBuyRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal budgetMin,
            @RequestParam(required = false) BigDecimal budgetMax,
            @RequestParam(required = false) String expectedCondition,
            @RequestParam(required = false) String contactInfo) {
        BuyRequestDTO request = buyRequestService.updateBuyRequest(
                id, userDetails.getId(), title, description, category, budgetMin, budgetMax,
                expectedCondition, contactInfo);
        return ResponseEntity.ok(ApiResponse.success("更新成功", request));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<Void>> closeBuyRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        buyRequestService.closeBuyRequest(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("已关闭", null));
    }

    @GetMapping("/{id}/responses")
    public ResponseEntity<ApiResponse<PageResponse<BuyRequestResponseDTO>>> getResponses(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<BuyRequestResponseDTO> responses = buyRequestService.getResponses(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<ApiResponse<BuyRequestResponseDTO>> createResponse(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) BigDecimal offeredPrice) {
        BuyRequestResponseDTO response = buyRequestService.createResponse(
                id, userDetails.getId(), productId, message, offeredPrice);
        return ResponseEntity.ok(ApiResponse.success("响应成功", response));
    }

    @PutMapping("/responses/{responseId}/accept")
    public ResponseEntity<ApiResponse<BuyRequestResponseDTO>> acceptResponse(
            @PathVariable Long responseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        BuyRequestResponseDTO response = buyRequestService.acceptResponse(responseId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("已接受", response));
    }

    @PutMapping("/responses/{responseId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectResponse(
            @PathVariable Long responseId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        buyRequestService.rejectResponse(responseId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("已拒绝", null));
    }
}
