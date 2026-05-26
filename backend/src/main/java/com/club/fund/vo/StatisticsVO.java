package com.club.fund.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StatisticsVO {

    private Long clubId;

    private String clubName;

    private BigDecimal totalIncome;

    private BigDecimal totalExpense;

    private BigDecimal balance;

    private Integer memberCount;

    private Integer activityCount;

    private Integer pendingApprovalCount;
}
