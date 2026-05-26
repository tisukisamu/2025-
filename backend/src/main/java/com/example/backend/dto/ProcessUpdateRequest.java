package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProcessUpdateRequest {
    
    @NotBlank(message = "阶段不能为空")
    private String stage;
    
    @NotBlank(message = "状态不能为空")
    private String status;
    
    private String description;
    
    private List<String> photos;
    
    private List<String> videos;
}
