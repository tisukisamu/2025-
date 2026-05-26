package com.agri.store.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column
    private Long parentId = 0L; // 父分类ID，0表示顶级分类

    @Column(nullable = false)
    private Integer level = 1; // 分类层级

    @Column(nullable = false)
    private Integer sortOrder = 0; // 排序

    @Column(length = 255)
    private String iconUrl; // 分类图标

    @Column(nullable = false)
    private Boolean active = true;

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
