package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRankingDTO {
    private Integer rank;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long earnedCount;
    private Long totalAchievements;
    private Double completionRate;
    private LocalDateTime latestEarnedAt;
    private Boolean currentUser;
}
