package com.club.fund.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalStepVO {

    private Integer step;

    private String stepName;

    private String status;

    private String approverName;

    private String comment;

    private LocalDateTime approvalTime;
}
