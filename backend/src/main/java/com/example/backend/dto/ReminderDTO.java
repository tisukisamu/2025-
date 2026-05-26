package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReminderDTO {

    private Long habitId;
    private String habitName;
    private String icon;
    private String color;
    private LocalTime reminderTime;
    private Boolean checked;
}

