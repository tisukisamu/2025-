package com.example.backend.dto;

import com.example.backend.entity.CommunityComment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private LocalDateTime createdAt;

    public static CommunityCommentDTO fromEntity(CommunityComment comment) {
        return CommunityCommentDTO.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getId())
                .userName(comment.getUser().getName())
                .userAvatar(comment.getUser().getAvatar())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
