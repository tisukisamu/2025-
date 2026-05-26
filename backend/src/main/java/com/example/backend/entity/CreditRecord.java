package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "credit_records")
public class CreditRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private CreditType type;
    
    @Column(nullable = false)
    private Integer points;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "related_id")
    private Long relatedId;
    
    @Column(name = "related_type")
    private String relatedType;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
    
    public enum CreditType {
        REGISTER,
        COMPLETE_PROFILE,
        PUBLISH_PRODUCT,
        SUCCESSFUL_TRADE,
        POSITIVE_REVIEW,
        NEGATIVE_REVIEW,
        CANCEL_ORDER,
        REPORT_VERIFIED,
        BE_REPORTED,
        DAILY_LOGIN,
        SHARE_TOPIC,
        RECEIVE_LIKE,
        SYSTEM_ADJUST
    }
}
