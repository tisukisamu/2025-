package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "operation_logs")
@Data
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String module; // 操作模块

    @Column(nullable = false, length = 100)
    private String action; // 操作类型

    @Column(length = 500)
    private String description; // 操作描述

    @Column(length = 2000)
    private String requestData; // 请求数据

    @Column(length = 2000)
    private String responseData; // 响应数据

    @Column(length = 50)
    private String ipAddress; // IP地址

    @Column(length = 500)
    private String userAgent; // 浏览器信息

    @Column(nullable = false)
    private Boolean success = true;

    @Column(length = 500)
    private String errorMessage;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }
}
