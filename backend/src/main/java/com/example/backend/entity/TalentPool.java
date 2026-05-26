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
@Table(name = "talent_pool")
public class TalentPool {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "resume_id", nullable = false)
    private Long resumeId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 500)
    private String tags;

    @Column(name = "group_id")
    private Long groupId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.COLLECTED;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Transient
    private Integer matchScore;

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        COLLECTED, CONTACTED, INTERVIEWED, BLACKLISTED
    }
}
