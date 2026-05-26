package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_logs")
@Data
public class StockLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false)
    private Integer changeAmount; // 变更数量（正数增加，负数减少）

    @Column(nullable = false)
    private Integer beforeStock;

    @Column(nullable = false)
    private Integer afterStock;

    @Column(length = 50)
    private String type; // SALE-销售, RESTOCK-补货, ADJUST-调整, RETURN-退货

    @Column(length = 500)
    private String remark;

    @Column(nullable = false)
    private Long operatorId;

    @Column(nullable = false)
    private String operatorName;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
