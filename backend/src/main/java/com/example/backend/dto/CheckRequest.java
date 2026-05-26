package com.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckRequest {
    
    @NotNull(message = "习惯ID不能为空")
    private Long habitId;
    
    private String note;
}
