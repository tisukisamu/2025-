package com.club.fund.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FundApplyResponse {

    private Long id;

    private String applyNo;

    private ClubResponse club;

    private UserResponse applicant;

    private String applyType;

    private BigDecimal amount;

    private String reason;

    private List<String> vouchers;

    private ActivityResponse activity;

    private String status;

    private Integer currentStep;

    private List<ApprovalRecordResponse> approvalRecords;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
