package com.agri.store.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuditRequest {
    @NotNull(message = "审核状态不能为空")
    private Integer status; // 1-通过, 2-驳回

    @Size(max = 500, message = "审核意见长度不能超过500")
    private String reason;
}
