package com.example.backend.dto.request;

import com.example.backend.entity.Notification;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    
    @NotBlank(message = "通知标题不能为空")
    private String title;
    
    private String content;
    
    private Notification.Type type = Notification.Type.GENERAL;
    
    private Notification.TargetRole targetRole = Notification.TargetRole.ALL;
    
    private Boolean isPublished = false;
}
