package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "buy_request_responses")
public class BuyRequestResponse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buy_request_id", nullable = false)
    private BuyRequest buyRequest;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responder_id", nullable = false)
    private User responder;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    
    @Column(columnDefinition = "TEXT")
    private String message;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal offeredPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ResponseStatus status = ResponseStatus.PENDING;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
    
    public enum ResponseStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
        WITHDRAWN
    }
}
