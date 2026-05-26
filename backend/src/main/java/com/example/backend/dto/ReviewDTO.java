package com.example.backend.dto;

import com.example.backend.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    
    private Long id;
    private Long orderId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Integer rating;
    private String content;
    private String images;
    private Boolean isAnonymous;
    private LocalDateTime createTime;
    
    public static ReviewDTO fromEntity(Review review) {
        String userName = null;
        String userAvatar = null;
        
        if (review.getUser() != null) {
            if (Boolean.TRUE.equals(review.getIsAnonymous())) {
                userName = "匿名用户";
            } else {
                userName = review.getUser().getUsername();
                userAvatar = review.getUser().getAvatar();
            }
        }
        
        return ReviewDTO.builder()
                .id(review.getId())
                .orderId(review.getOrder() != null ? review.getOrder().getId() : null)
                .userId(review.getUser() != null ? review.getUser().getId() : null)
                .userName(userName)
                .userAvatar(userAvatar)
                .rating(review.getRating())
                .content(review.getContent())
                .images(review.getImages())
                .isAnonymous(review.getIsAnonymous())
                .createTime(review.getCreateTime())
                .build();
    }
}
