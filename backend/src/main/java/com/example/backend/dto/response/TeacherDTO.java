package com.example.backend.dto.response;

import com.example.backend.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDTO {
    
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private String subjects;
    private Integer experience;
    private String bio;
    private String avatar;
    private Teacher.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
