package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.entity.Notification;
import com.club.fund.service.NotificationService;
import com.club.fund.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/list")
    public Result<Page<Notification>> getNotificationList(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size);
        
        if (type != null) {
            return Result.success(notificationService.getNotificationByType(userId, type, pageable));
        }
        return Result.success(notificationService.getNotificationList(userId, pageable));
    }

    @GetMapping("/{id}")
    public Result<Notification> getNotificationById(@PathVariable Long id) {
        return Result.success(notificationService.getNotificationById(id));
    }

    @GetMapping("/unread-count")
    public Result<Integer> getUnreadCount() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(notificationService.getUnreadCount(userId));
    }

    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success();
    }

    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return Result.success();
    }
}
