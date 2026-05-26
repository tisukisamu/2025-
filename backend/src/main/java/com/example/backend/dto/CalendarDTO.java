package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDTO {
    
    private String month;
    private List<DayRecordDTO> days;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayRecordDTO {
        private int day;
        private String date;
        private int totalHabits;
        private int completedHabits;
        private double completeRate;
        private List<HabitCheckDTO> habits;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HabitCheckDTO {
        private Long habitId;
        private String habitName;
        private boolean checked;
    }
}
