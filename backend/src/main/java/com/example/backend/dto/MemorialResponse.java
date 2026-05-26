package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MemorialResponse {
    
    private Long id;
    
    private Long petId;
    
    private Long userId;
    
    private String title;
    
    private String description;
    
    private List<String> photos;
    
    private Integer isPublic;
    
    private Integer viewCount;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String petName;
    
    private String userName;
}
