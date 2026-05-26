package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resumes")
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 10)
    private String gender;

    private Integer age;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String education;

    @Column(length = 20)
    private String experience;

    @Column(columnDefinition = "TEXT")
    private String skills;

    @Column(name = "work_experience", columnDefinition = "TEXT")
    private String workExperience;

    @Column(name = "project_experience", columnDefinition = "TEXT")
    private String projectExperience;

    @Column(name = "education_experience", columnDefinition = "TEXT")
    private String educationExperience;

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
}
