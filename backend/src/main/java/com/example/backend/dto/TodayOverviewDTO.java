package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayOverviewDTO {
    
    private int totalHabits;
    private int completedHabits;
    private int pendingHabits;
    private double completeRate;
    private List<HabitWithStatusDTO> habits;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitWithStatusDTO {
        private Long id;
        private String name;
        private String icon;
        private String color;
        private boolean checked;
        private int streakDays;
    }
}
