package com.club.fund.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FundApplyRequest {

    @NotNull(message = "社团ID不能为空")
    private Long clubId;

    @NotBlank(message = "申请类型不能为空")
    private String applyType;

    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.01", message = "申请金额必须大于0")
    private BigDecimal amount;

    @NotBlank(message = "申请理由不能为空")
    @Size(min = 20, message = "申请理由不能少于20字")
    private String reason;

    private List<String> vouchers;

    private Long activityId;
}
