package com.example.backend.dto;

import com.example.backend.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderAvatar;
    private Long receiverId;
    private String receiverName;
    private String receiverAvatar;
    private String content;
    private Message.MessageType type;
    private Boolean isRead;
    private LocalDateTime createTime;
    
    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSender() != null ? message.getSender().getId() : null)
                .senderName(message.getSender() != null ? message.getSender().getUsername() : null)
                .senderAvatar(message.getSender() != null ? message.getSender().getAvatar() : null)
                .receiverId(message.getReceiver() != null ? message.getReceiver().getId() : null)
                .receiverName(message.getReceiver() != null ? message.getReceiver().getUsername() : null)
                .receiverAvatar(message.getReceiver() != null ? message.getReceiver().getAvatar() : null)
                .content(message.getContent())
                .type(message.getType())
                .isRead(message.getIsRead())
                .createTime(message.getCreateTime())
                .build();
    }
}
