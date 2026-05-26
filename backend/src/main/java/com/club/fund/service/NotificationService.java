package com.club.fund.service;

import com.club.fund.entity.Notification;
import com.club.fund.entity.User;
import com.club.fund.exception.BusinessException;
import com.club.fund.repository.NotificationRepository;
import com.club.fund.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public Page<Notification> getNotificationList(Long receiverId, Pageable pageable) {
        return notificationRepository.findByReceiverId(receiverId, pageable);
    }

    public Page<Notification> getNotificationByType(Long receiverId, String type, Pageable pageable) {
        return notificationRepository.findByReceiverIdAndType(receiverId, type, pageable);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("通知不存在"));
    }

    public int getUnreadCount(Long receiverId) {
        return notificationRepository.countUnreadByReceiverId(receiverId);
    }

    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.markAsRead(id);
    }

    @Transactional
    public void markAllAsRead(Long receiverId) {
        notificationRepository.markAllAsRead(receiverId);
    }

    @Transactional
    public void sendNotification(String title, String content, String type, Long receiverId, Long relatedId, String relatedType) {
        User receiver = userRepository.findById(receiverId).orElse(null);
        
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setReceiver(receiver);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setIsRead(0);
        notificationRepository.save(notification);
    }
}
