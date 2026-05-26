package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsDTO {
    
    private Long id;
    private Long habitId;
    private String habitName;
    private Integer totalDays;
    private Integer streakDays;
    private Integer maxStreak;
    private BigDecimal completeRate;
    private LocalDate lastCheckDate;
}
