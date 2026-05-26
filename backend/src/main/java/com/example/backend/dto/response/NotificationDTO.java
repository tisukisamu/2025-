package com.example.backend.dto.response;

import com.example.backend.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    
    private Long id;
    private String title;
    private String content;
    private Notification.Type type;
    private Notification.TargetRole targetRole;
    private Boolean isPublished;
    private LocalDateTime publishedAt;
    private UserDTO createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
