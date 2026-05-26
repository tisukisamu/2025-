package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResumeDTO {
    private Long id;
    
    private Long userId;
    
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String name;
    
    @Size(max = 10, message = "性别不能超过10个字符")
    private String gender;
    
    private Integer age;
    
    @Size(max = 20, message = "电话不能超过20个字符")
    private String phone;
    
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过100个字符")
    private String email;
    
    @Size(max = 20, message = "学历不能超过20个字符")
    private String education;
    
    @Size(max = 20, message = "工作经验不能超过20个字符")
    private String experience;
    
    private String skills;
    
    private String workExperience;
    
    private String projectExperience;
    
    private String educationExperience;
}
