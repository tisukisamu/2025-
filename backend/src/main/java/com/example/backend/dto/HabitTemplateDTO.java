package com.example.backend.dto;

import com.example.backend.entity.Habit;
import com.example.backend.entity.HabitTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitTemplateDTO {

    private Long id;
    private String name;
    private String description;
    private String icon;
    private String color;
    private Habit.RepeatType repeatType;
    private String repeatDays;
    private LocalTime reminderTime;
    private String categoryName;
    private String categoryColor;
    private String tags;

    public static HabitTemplateDTO fromEntity(HabitTemplate template) {
        return HabitTemplateDTO.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .icon(template.getIcon())
                .color(template.getColor())
                .repeatType(template.getRepeatType())
                .repeatDays(template.getRepeatDays())
                .reminderTime(template.getReminderTime())
                .categoryName(template.getCategoryName())
                .categoryColor(template.getCategoryColor())
                .tags(template.getTags())
                .build();
    }
}

