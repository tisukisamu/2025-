package com.example.backend.dto.request;

import com.example.backend.entity.Student;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequest {
    
    private Long userId;
    
    @NotBlank(message = "学员姓名不能为空")
    private String name;
    
    private String phone;
    
    private String email;
    
    private Student.Gender gender;
    
    private LocalDate birthDate;
    
    private String emergencyContact;
    
    private String emergencyPhone;
    
    private String avatar;
    
    private Student.Status status = Student.Status.ACTIVE;
}
