package com.example.backend.dto;

import com.example.backend.entity.Habit;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDTO {
    
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Habit.RepeatType repeatType;
    private String repeatDays;
    private LocalTime reminderTime;
    private Boolean reminderEnabled;
    private Habit.Status status;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static HabitDTO fromEntity(Habit habit) {
        return HabitDTO.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .icon(habit.getIcon())
                .color(habit.getColor())
                .repeatType(habit.getRepeatType())
                .repeatDays(habit.getRepeatDays())
                .reminderTime(habit.getReminderTime())
                .reminderEnabled(habit.getReminderEnabled())
                .status(habit.getStatus())
                .categoryId(habit.getCategory() != null ? habit.getCategory().getId() : null)
                .categoryName(habit.getCategory() != null ? habit.getCategory().getName() : null)
                .createdAt(habit.getCreatedAt())
                .updatedAt(habit.getUpdatedAt())
                .build();
    }
}
