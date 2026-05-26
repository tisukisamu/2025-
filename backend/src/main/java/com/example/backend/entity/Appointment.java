package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "订单号不能为空")
    @Column(name = "order_no", nullable = false, unique = true, length = 50)
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "宠物ID不能为空")
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @NotNull(message = "套餐ID不能为空")
    @Column(name = "package_id", nullable = false)
    private Long packageId;

    @NotNull(message = "预约时间不能为空")
    @Column(name = "appointment_time", nullable = false)
    private LocalDateTime appointmentTime;

    @NotBlank(message = "联系人不能为空")
    @Column(name = "contact_name", nullable = false, length = 50)
    private String contactName;

    @NotBlank(message = "联系电话不能为空")
    @Column(name = "contact_phone", nullable = false, length = 20)
    private String contactPhone;

    private String address;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(nullable = false, length = 20)
    private String status = "pending";

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Integer deleted = 0;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
