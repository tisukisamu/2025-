package com.example.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompanyRatingDTO {
    private Long id;
    
    @NotNull(message = "企业ID不能为空")
    private Long companyId;
    
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 5, message = "评分最大为5")
    private Integer rating;
    
    private String comment;
}
