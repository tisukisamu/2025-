package com.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String password;
    
    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名长度不能超过50个字符")
    private String name;
    
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @NotNull(message = "年龄不能为空")
    private Integer age;
}
