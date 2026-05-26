package com.example.backend.controller;

import com.example.backend.dto.FeedbackDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Feedback;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<FeedbackDTO>>> getAllFeedbacks(
            @RequestParam(required = false) Feedback.FeedbackStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FeedbackDTO> feedbacks = feedbackService.getAllFeedbacks(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<FeedbackDTO>>> getMyFeedbacks(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<FeedbackDTO> feedbacks = feedbackService.getMyFeedbacks(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackDTO>> create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Feedback.FeedbackType type,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String contactInfo,
            @RequestParam(required = false) String images) {
        FeedbackDTO feedback = feedbackService.create(
                userDetails.getId(), type, title, content, contactInfo, images);
        return ResponseEntity.ok(ApiResponse.success("反馈提交成功", feedback));
    }

    @PutMapping("/{id}/reply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<FeedbackDTO>> reply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String replyContent) {
        FeedbackDTO feedback = feedbackService.reply(id, userDetails.getId(), replyContent);
        return ResponseEntity.ok(ApiResponse.success("回复成功", feedback));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable Long id,
            @RequestParam Feedback.FeedbackStatus status) {
        feedbackService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", null));
    }
}
