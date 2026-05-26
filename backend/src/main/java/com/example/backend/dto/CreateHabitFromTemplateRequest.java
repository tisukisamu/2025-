package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateHabitFromTemplateRequest {

    private String name;

    private LocalTime reminderTime;

    private Boolean reminderEnabled;

    private Long categoryId;
}

