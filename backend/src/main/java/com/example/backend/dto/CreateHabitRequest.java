package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitRequest {
    
    @NotBlank(message = "习惯名称不能为空")
    private String name;
    
    private String description;
    
    private String icon;
    
    private String color;
    
    @NotNull(message = "重复类型不能为空")
    private String repeatType;
    
    private String repeatDays;
    
    private LocalTime reminderTime;

    private Boolean reminderEnabled;
    
    private Long categoryId;
}
