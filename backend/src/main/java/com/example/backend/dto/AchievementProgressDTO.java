package com.example.backend.dto;

import com.example.backend.entity.Achievement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementProgressDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String icon;
    private Achievement.ConditionType conditionType;
    private Integer threshold;
    private Long currentValue;
    private Double progressPercent;
    private Boolean earned;
    private LocalDateTime earnedAt;
}

