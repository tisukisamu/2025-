package com.example.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {
    
    @NotNull(message = "宠物ID不能为空")
    private Long petId;
    
    @NotNull(message = "套餐ID不能为空")
    private Long packageId;
    
    @NotNull(message = "预约时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appointmentTime;
    
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    
    @NotBlank(message = "联系电话不能为空")
    private String contactPhone;
    
    private String address;
    
    private String remark;
}
