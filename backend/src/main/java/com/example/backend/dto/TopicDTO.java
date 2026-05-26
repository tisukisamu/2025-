package com.example.backend.dto;

import com.example.backend.entity.Topic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    
    private Long id;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String title;
    private String content;
    private String category;
    private String coverImage;
    private Boolean isPinned;
    private Boolean isHot;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Topic.TopicStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isLiked;
    
    public static TopicDTO fromEntity(Topic topic) {
        return TopicDTO.builder()
                .id(topic.getId())
                .authorId(topic.getAuthor() != null ? topic.getAuthor().getId() : null)
                .authorName(topic.getAuthor() != null ? topic.getAuthor().getUsername() : null)
                .authorAvatar(topic.getAuthor() != null ? topic.getAuthor().getAvatar() : null)
                .title(topic.getTitle())
                .content(topic.getContent())
                .category(topic.getCategory())
                .coverImage(topic.getCoverImage())
                .isPinned(topic.getIsPinned())
                .isHot(topic.getIsHot())
                .viewCount(topic.getViewCount())
                .likeCount(topic.getLikeCount())
                .commentCount(topic.getCommentCount())
                .status(topic.getStatus())
                .createTime(topic.getCreateTime())
                .updateTime(topic.getUpdateTime())
                .isLiked(false)
                .build();
    }
}
