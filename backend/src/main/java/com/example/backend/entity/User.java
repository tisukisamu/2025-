package com.example.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    @Column(nullable = false, length = 255)
    private String password;

    @NotBlank(message = "姓名不能为空")
    @Column(nullable = false, length = 50)
    private String name;

    @Email(message = "邮箱格式不正确")
    @Column(length = 100)
    private String email;

    @NotNull(message = "年龄不能为空")
    @Column(nullable = false)
    private Integer age;

    @Size(max = 255, message = "头像地址不能超过255个字符")
    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    public String getAvatarUrl() {
        if (avatarUrl == null || avatarUrl.isBlank()) {
            return "/default-avatar.svg";
        }
        return avatarUrl;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN, USER
    }

    public enum Status {
        ACTIVE, DISABLED
    }
}
