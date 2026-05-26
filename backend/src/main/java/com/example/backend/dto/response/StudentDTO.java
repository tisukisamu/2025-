package com.example.backend.dto.response;

import com.example.backend.entity.Student;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDTO {
    
    private Long id;
    private Long userId;
    private String name;
    private String phone;
    private String email;
    private Student.Gender gender;
    private LocalDate birthDate;
    private String emergencyContact;
    private String emergencyPhone;
    private String avatar;
    private Student.Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
