package com.example.backend.dto;

import com.example.backend.entity.Appointment;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AppointmentResponse {
    
    private Long id;
    
    private String orderNo;
    
    private Long userId;
    
    private Long petId;
    
    private Long packageId;
    
    private LocalDateTime appointmentTime;
    
    private String contactName;
    
    private String contactPhone;
    
    private String address;
    
    private String remark;
    
    private String status;
    
    private Long operatorId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String petName;
    
    private String petType;
    
    private String petPhoto;
    
    private String packageName;
    
    private BigDecimal packagePrice;
    
    public static AppointmentResponse fromEntity(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setOrderNo(appointment.getOrderNo());
        response.setUserId(appointment.getUserId());
        response.setPetId(appointment.getPetId());
        response.setPackageId(appointment.getPackageId());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setContactName(appointment.getContactName());
        response.setContactPhone(appointment.getContactPhone());
        response.setAddress(appointment.getAddress());
        response.setRemark(appointment.getRemark());
        response.setStatus(appointment.getStatus());
        response.setOperatorId(appointment.getOperatorId());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        return response;
    }
}
