package com.club.fund.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "club")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "club_name", nullable = false, length = 100)
    private String clubName;

    @Column(name = "club_code", nullable = false, unique = true, length = 20)
    private String clubCode;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String logo;

    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "president_id")
    private User president;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    private Integer deleted = 0;

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<ClubMember> members;
}
