package com.example.backend.controller;

import com.example.backend.dto.MessageDTO;
import com.example.backend.entity.Message;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMessages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messageService.getMessages(userDetails.getId(), pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadMessages(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        List<Message> messages = messageService.getUnreadMessages(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", messages);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        long count = messageService.getUnreadCount(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMessageById(@PathVariable Long id) {
        Message message = messageService.getMessageById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", message);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        messageService.markAsRead(id, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "标记成功");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        messageService.markAllAsRead(userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "全部标记成功");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        messageService.deleteMessage(id, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
