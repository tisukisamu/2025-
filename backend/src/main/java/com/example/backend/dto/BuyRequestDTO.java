package com.example.backend.dto;

import com.example.backend.entity.BuyRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyRequestDTO {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String title;
    private String description;
    private String category;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String expectedCondition;
    private String contactInfo;
    private BuyRequest.BuyRequestStatus status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime expireTime;
    private Integer viewCount;
    private Integer responseCount;
    
    public static BuyRequestDTO fromEntity(BuyRequest request) {
        return BuyRequestDTO.builder()
                .id(request.getId())
                .userId(request.getUser() != null ? request.getUser().getId() : null)
                .userName(request.getUser() != null ? request.getUser().getUsername() : null)
                .userAvatar(request.getUser() != null ? request.getUser().getAvatar() : null)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .budgetMin(request.getBudgetMin())
                .budgetMax(request.getBudgetMax())
                .expectedCondition(request.getExpectedCondition())
                .contactInfo(request.getContactInfo())
                .status(request.getStatus())
                .createTime(request.getCreateTime())
                .updateTime(request.getUpdateTime())
                .expireTime(request.getExpireTime())
                .viewCount(request.getViewCount())
                .responseCount(request.getResponseCount())
                .build();
    }
}
