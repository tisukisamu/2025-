package com.club.fund.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "sys_log")
public class SysLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 50)
    private String username;

    @Column(length = 100)
    private String operation;

    @Column(length = 200)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Column(length = 50)
    private String ip;

    private Integer status;

    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    private Long duration;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}
