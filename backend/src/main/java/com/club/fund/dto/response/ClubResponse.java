package com.club.fund.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClubResponse {

    private Long id;

    private String clubName;

    private String clubCode;

    private String description;

    private String logo;

    private String category;

    private UserResponse president;

    private UserResponse teacher;

    private BigDecimal balance;

    private Integer memberCount;

    private Integer status;

    private LocalDateTime createTime;

    private BigDecimal monthIncome;

    private BigDecimal monthExpense;

    private Integer pendingApprovalCount;
}
