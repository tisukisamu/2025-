package com.example.backend.dto;

import com.example.backend.entity.TopicComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicCommentDTO {
    
    private Long id;
    private Long topicId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long parentId;
    private String parentUserName;
    private String content;
    private Integer likeCount;
    private TopicComment.CommentStatus status;
    private LocalDateTime createTime;
    private List<TopicCommentDTO> replies;
    
    public static TopicCommentDTO fromEntity(TopicComment comment) {
        return TopicCommentDTO.builder()
                .id(comment.getId())
                .topicId(comment.getTopic() != null ? comment.getTopic().getId() : null)
                .userId(comment.getUser() != null ? comment.getUser().getId() : null)
                .userName(comment.getUser() != null ? comment.getUser().getUsername() : null)
                .userAvatar(comment.getUser() != null ? comment.getUser().getAvatar() : null)
                .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .parentUserName(comment.getParent() != null && comment.getParent().getUser() != null 
                        ? comment.getParent().getUser().getUsername() : null)
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .status(comment.getStatus())
                .createTime(comment.getCreateTime())
                .build();
    }
}
