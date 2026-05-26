package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(max = 50, message = "真实姓名不能超过50个字符")
    private String realName;

    @Size(max = 20, message = "手机号不能超过20个字符")
    private String phone;

    private String avatar;
}
