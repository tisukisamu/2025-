package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CompanyDTO {
    private Long id;
    
    @NotBlank(message = "企业名称不能为空")
    @Size(max = 100, message = "企业名称不能超过100个字符")
    private String name;
    
    private String description;
    
    @Size(max = 50, message = "行业不能超过50个字符")
    private String industry;
    
    @Size(max = 20, message = "规模不能超过20个字符")
    private String scale;
    
    @Size(max = 200, message = "地址不能超过200个字符")
    private String address;
    
    @Size(max = 50, message = "联系人不能超过50个字符")
    private String contactPerson;
    
    @Size(max = 20, message = "联系电话不能超过20个字符")
    private String contactPhone;
    
    @Size(max = 100, message = "联系邮箱不能超过100个字符")
    private String contactEmail;

    @Size(max = 255, message = "Logo地址不能超过255个字符")
    private String logoUrl;
}
