package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHabitRequest {
    
    private String name;
    
    private String description;
    
    private String icon;
    
    private String color;
    
    private String repeatType;
    
    private String repeatDays;
    
    private LocalTime reminderTime;

    private Boolean reminderEnabled;
    
    private Long categoryId;
}
