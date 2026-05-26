package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewDTO {
    private Long id;
    
    @NotNull(message = "投递记录ID不能为空")
    private Long applicationId;
    
    @NotNull(message = "面试时间不能为空")
    private LocalDateTime interviewTime;
    
    @NotBlank(message = "面试地点不能为空")
    private String location;
    
    private String interviewer;
    
    private String result;
    
    private String feedback;
}
