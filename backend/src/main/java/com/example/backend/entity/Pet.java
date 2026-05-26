package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pet_info")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "宠物名称不能为空")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "宠物类型不能为空")
    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 50)
    private String breed;

    @Column(length = 10)
    private String gender;

    private LocalDate birthday;

    @Column(name = "pass_date")
    private LocalDate passDate;

    private String photo;

    private String color;

    private BigDecimal weight;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "memorial_text", columnDefinition = "TEXT")
    private String memorialText;

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
