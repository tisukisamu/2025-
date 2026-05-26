package com.example.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequest {
    
    @NotNull(message = "课程ID不能为空")
    private Long courseId;
    
    private Long scheduleId;
    
    private Long studentId;
}
