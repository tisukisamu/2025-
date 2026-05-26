package com.example.backend.dto.response;

import com.example.backend.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleDTO {
    
    private Long id;
    private CourseDTO course;
    private TeacherDTO teacher;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private String room;
    private Boolean isRecurring;
    private String recurrencePattern;
    private Schedule.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
