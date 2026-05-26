package com.example.backend.controller;

import com.example.backend.dto.MessageDTO;
import com.example.backend.dto.request.MessageRequest;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<MessageDTO>>> getMessages(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<MessageDTO> messages = messageService.getMessages(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getConversation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId) {
        List<MessageDTO> messages = messageService.getConversation(userDetails.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody MessageRequest request) {
        MessageDTO message = messageService.sendMessage(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @PutMapping("/read/{userId}")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long userId) {
        messageService.markAsRead(userDetails.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        long count = messageService.getUnreadCount(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
