package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer stock;
    
    @Column(name = "stock_warning", nullable = false)
    private Integer stockWarning = 10; // 库存预警

    @Column(length = 500)
    private String imageUrl; // 主图
    
    @Column(length = 2000)
    private String imageUrls; // 多图，逗号分隔

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 100)
    private String category; // 旧分类字段，保留用于兼容

    @Column
    private Long categoryId; // 新分类ID关联

    @Column(nullable = false)
    private Boolean isNew = false;

    @Column(nullable = false)
    private Boolean isHot = false;

    @Column(nullable = false)
    private Integer sales = 0;

    // 店家相关字段
    @Column
    private Long storeId;  // 所属店铺ID，null表示平台自营

    @Column(nullable = false)
    private Integer status = 1;  // 0-待审核, 1-已通过, 2-已驳回

    @Column(length = 500)
    private String rejectReason;  // 驳回原因
    
    @Column(name = "audit_time")
    private LocalDateTime auditTime;

    @Column(name = "audit_by")
    private Long auditBy;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}
