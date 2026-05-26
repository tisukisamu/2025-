package com.example.backend.dto.response;

import com.example.backend.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDTO {
    
    private Long id;
    private String name;
    private String description;
    private TeacherDTO teacher;
    private String category;
    private Course.Level level;
    private Integer duration;
    private BigDecimal price;
    private Integer capacity;
    private Integer enrolledCount;
    private String image;
    private Course.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
