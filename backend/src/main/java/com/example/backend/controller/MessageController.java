package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.MessageRequest;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.Message;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/album/{albumId}")
    public Result<List<Message>> getAlbumMessages(@PathVariable Long albumId) {
        List<Message> messages = messageService.getAlbumMessages(albumId);
        return Result.success(messages);
    }

    @GetMapping("/album/{albumId}/page")
    public Result<PageResponse<Message>> getAlbumMessagesPage(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<Message> result = messageService.getAlbumMessages(albumId, pageNum, pageSize);
        return Result.success(result);
    }

    @PostMapping
    public Result<Message> createMessage(@Valid @RequestBody MessageRequest request) {
        Message message = messageService.createMessage(request);
        return Result.success("留言成功", message);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMessage(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        messageService.deleteMessage(id, userId);
        return Result.success("删除成功", null);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
