package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PetRequest {
    
    @NotBlank(message = "宠物名称不能为空")
    private String name;
    
    @NotBlank(message = "宠物类型不能为空")
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
}
