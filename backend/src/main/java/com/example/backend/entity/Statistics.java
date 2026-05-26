package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statistics",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"user_id", "habit_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Statistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "total_days")
    private Integer totalDays = 0;

    @Column(name = "streak_days")
    private Integer streakDays = 0;

    @Column(name = "max_streak")
    private Integer maxStreak = 0;

    @Column(name = "complete_rate", precision = 5, scale = 2)
    private BigDecimal completeRate = BigDecimal.ZERO;

    @Column(name = "last_check_date")
    private LocalDate lastCheckDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
