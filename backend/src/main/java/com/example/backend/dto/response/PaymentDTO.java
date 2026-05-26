package com.example.backend.dto.response;

import com.example.backend.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    
    private Long id;
    private EnrollmentDTO enrollment;
    private BigDecimal amount;
    private Payment.PaymentMethod paymentMethod;
    private String transactionId;
    private Payment.Status status;
    private LocalDateTime paymentTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
