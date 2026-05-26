package com.club.fund.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FundFlowVO {

    private Long id;

    private String flowNo;

    private String flowType;

    private BigDecimal amount;

    private BigDecimal balanceBefore;

    private BigDecimal balanceAfter;

    private String description;

    private String operatorName;

    private String applyNo;

    private LocalDateTime createTime;
}
