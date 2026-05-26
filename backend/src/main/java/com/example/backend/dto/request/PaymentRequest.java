package com.example.backend.dto.request;

import com.example.backend.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    private Long enrollmentId;
    
    private Long billId;
    
    @NotNull(message = "支付金额不能为空")
    @DecimalMin(value = "0.0", message = "金额不能为负数")
    private BigDecimal amount;
    
    @NotNull(message = "支付方式不能为空")
    private Payment.PaymentMethod paymentMethod;
}
