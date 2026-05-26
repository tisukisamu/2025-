package com.agri.store.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShipRequest {
    @NotBlank(message = "物流单号不能为空")
    private String trackingNo;
}
