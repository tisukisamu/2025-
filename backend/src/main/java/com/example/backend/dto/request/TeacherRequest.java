package com.example.backend.dto.request;

import com.example.backend.entity.Teacher;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherRequest {
    
    private Long userId;
    
    @NotBlank(message = "教师姓名不能为空")
    private String name;
    
    private String phone;
    
    private String email;
    
    private String subjects;
    
    private Integer experience;
    
    private String bio;
    
    private String avatar;
    
    private Teacher.Status status = Teacher.Status.ACTIVE;
}
