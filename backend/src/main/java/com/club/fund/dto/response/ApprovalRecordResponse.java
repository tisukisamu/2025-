package com.club.fund.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApprovalRecordResponse {

    private Long id;

    private Integer step;

    private String approverName;

    private String action;

    private String comment;

    private LocalDateTime createTime;
}
