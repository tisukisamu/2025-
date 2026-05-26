package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckinRankingDTO {
    private Integer rank;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long totalCheckDays;
    private Integer maxStreak;
    private Double avgCompleteRate;
    private Double score;
    private Boolean currentUser;
}
