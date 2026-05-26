package com.example.backend.dto.request;

import com.example.backend.entity.Order;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "交易方式不能为空")
    private Order.TradeType tradeType;

    private String address;
}
