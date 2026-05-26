package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MemorialRequest {
    
    private Long petId;
    
    private String title;
    
    private String description;
    
    private List<String> photos;
    
    private Integer isPublic;
}
