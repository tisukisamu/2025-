package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "buy_requests")
public class BuyRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 50)
    private String category;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal budgetMin;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal budgetMax;
    
    @Column(length = 50)
    private String expectedCondition;
    
    @Column(length = 500)
    private String contactInfo;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BuyRequestStatus status = BuyRequestStatus.OPEN;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
    
    @UpdateTimestamp
    private LocalDateTime updateTime;
    
    @Column(name = "expire_time")
    private LocalDateTime expireTime;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "response_count")
    private Integer responseCount = 0;
    
    @OneToMany(mappedBy = "buyRequest", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BuyRequestResponse> responses = new ArrayList<>();
    
    public enum BuyRequestStatus {
        OPEN,
        IN_PROGRESS,
        CLOSED,
        FULFILLED
    }
    
    public enum ExpectedCondition {
        NEW,
        LIKE_NEW,
        GOOD,
        ACCEPTABLE,
        ANY
    }
}
