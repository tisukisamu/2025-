package com.example.backend.dto;

import com.example.backend.entity.CommunityPost;
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
public class CommunityPostDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private String imagePath;
    private Long commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommunityCommentDTO> comments;

    public static CommunityPostDTO fromEntity(CommunityPost post, Long commentCount, List<CommunityCommentDTO> comments) {
        return CommunityPostDTO.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .userName(post.getUser().getName())
                .userAvatar(post.getUser().getAvatar())
                .content(post.getContent())
                .imagePath(post.getImagePath())
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .comments(comments)
                .build();
    }
}
