package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alumni_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumniProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(length = 100)
    private String school;

    @Column(length = 100)
    private String major;

    @Column(name = "graduation_year")
    private Integer graduationYear;

    @Column(length = 100)
    private String city;

    @Column
    private Double latitude;

    @Column
    private Double longitude;

    @Column(length = 500)
    private String bio;

    @Column(name = "open_nearby", nullable = false)
    private Boolean openNearby = true;

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
