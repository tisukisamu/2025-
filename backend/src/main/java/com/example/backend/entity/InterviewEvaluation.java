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
@Table(name = "interview_evaluations")
public class InterviewEvaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @Column(name = "interviewer_id", nullable = false)
    private Long interviewerId;

    @Column(name = "evaluation_template_id")
    private Long evaluationTemplateId;

    @Column(nullable = false, columnDefinition = "JSON")
    private String scores;

    @Column(columnDefinition = "TEXT")
    private String comments;

    @Column(name = "overall_score")
    private Integer overallScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Recommendation recommendation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Recommendation {
        STRONGLY_RECOMMEND, RECOMMEND, NEUTRAL, NOT_RECOMMEND
    }
}
