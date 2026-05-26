package com.example.backend.dto.request;

import com.example.backend.entity.Course;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {
    
    @NotBlank(message = "课程名称不能为空")
    private String name;
    
    private String description;
    
    private Long teacherId;
    
    private String category;
    
    private Course.Level level;
    
    private Integer duration;
    
    @NotNull(message = "课程价格不能为空")
    @DecimalMin(value = "0.0", message = "价格不能为负数")
    private BigDecimal price;
    
    private Integer capacity = 20;
    
    private String image;
    
    private Course.Status status = Course.Status.DRAFT;
}
