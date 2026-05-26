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
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "contact_info")
    private String contactInfo;

    @Column(name = "images", columnDefinition = "JSON")
    private String images;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status = FeedbackStatus.PENDING;

    @Column(name = "reply_content")
    private String replyContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replier_id")
    private User replier;

    @Column(name = "reply_time")
    private LocalDateTime replyTime;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    public enum FeedbackType {
        BUG, SUGGESTION, COMPLAINT, OTHER
    }

    public enum FeedbackStatus {
        PENDING, PROCESSING, RESOLVED, CLOSED
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
