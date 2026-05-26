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
@Table(name = "question_bank")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Type type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String options;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Difficulty difficulty;

    @Column(length = 200)
    private String tags;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

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

    public enum Type {
        SINGLE_CHOICE, MULTIPLE_CHOICE, CODING, ESSAY
    }

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
