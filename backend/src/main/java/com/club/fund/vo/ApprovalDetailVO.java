package com.club.fund.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ApprovalDetailVO {

    private Long id;

    private String applyNo;

    private String clubName;

    private String applicantName;

    private String applyType;

    private BigDecimal amount;

    private String reason;

    private List<String> vouchers;

    private String status;

    private Integer currentStep;

    private List<ApprovalStepVO> approvalSteps;

    private LocalDateTime createTime;
}
