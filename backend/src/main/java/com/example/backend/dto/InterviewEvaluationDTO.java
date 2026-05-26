package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class InterviewEvaluationDTO {
    private Long id;
    
    @NotNull(message = "面试ID不能为空")
    private Long interviewId;
    
    @NotNull(message = "评分不能为空")
    private Map<String, Integer> scores;
    
    private String comments;
    
    private String recommendation;
}
