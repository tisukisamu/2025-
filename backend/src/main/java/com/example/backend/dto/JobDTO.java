package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class JobDTO {
    private Long id;
    
    private Long companyId;
    
    @NotBlank(message = "职位名称不能为空")
    @Size(max = 100, message = "职位名称不能超过100个字符")
    private String title;
    
    private String description;
    
    private String requirements;
    
    private BigDecimal salaryMin;
    
    private BigDecimal salaryMax;
    
    @Size(max = 100, message = "工作地点不能超过100个字符")
    private String location;
    
    @Size(max = 20, message = "工作类型不能超过20个字符")
    private String jobType;
    
    @Size(max = 20, message = "学历要求不能超过20个字符")
    private String education;
    
    @Size(max = 20, message = "经验要求不能超过20个字符")
    private String experience;

    @Size(max = 255, message = "封面地址不能超过255个字符")
    private String coverUrl;
    
    private String status;
}
