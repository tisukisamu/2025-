package com.example.backend.dto;

import com.example.backend.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Feedback.FeedbackType type;
    private String title;
    private String content;
    private String contactInfo;
    private String images;
    private Feedback.FeedbackStatus status;
    private String replyContent;
    private Long replierId;
    private String replierName;
    private LocalDateTime replyTime;
    private LocalDateTime createTime;
    
    public static FeedbackDTO fromEntity(Feedback feedback) {
        return FeedbackDTO.builder()
                .id(feedback.getId())
                .userId(feedback.getUser() != null ? feedback.getUser().getId() : null)
                .userName(feedback.getUser() != null ? feedback.getUser().getUsername() : null)
                .userAvatar(feedback.getUser() != null ? feedback.getUser().getAvatar() : null)
                .type(feedback.getType())
                .title(feedback.getTitle())
                .content(feedback.getContent())
                .contactInfo(feedback.getContactInfo())
                .images(feedback.getImages())
                .status(feedback.getStatus())
                .replyContent(feedback.getReplyContent())
                .replierId(feedback.getReplier() != null ? feedback.getReplier().getId() : null)
                .replierName(feedback.getReplier() != null ? feedback.getReplier().getUsername() : null)
                .replyTime(feedback.getReplyTime())
                .createTime(feedback.getCreateTime())
                .build();
    }
}
