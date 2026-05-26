package com.example.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PetResponse {
    
    private Long id;
    
    private Long userId;
    
    private String name;
    
    private String type;
    
    private String breed;
    
    private String gender;
    
    private LocalDate birthday;
    
    private LocalDate passDate;
    
    private String photo;
    
    private String color;
    
    private BigDecimal weight;
    
    private String description;
    
    private String memorialText;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
