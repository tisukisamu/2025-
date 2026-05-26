package com.example.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProcessResponse {
    
    private Long id;
    
    private Long appointmentId;
    
    private String stage;
    
    private String status;
    
    private Long operatorId;
    
    private String description;
    
    private List<String> photos;
    
    private List<String> videos;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
