package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationDTO {
    private Long id;
    
    private Long userId;
    
    @NotNull(message = "职位ID不能为空")
    private Long jobId;
    
    @NotNull(message = "简历ID不能为空")
    private Long resumeId;
    
    private String status;
}
