package com.example.backend.dto.response;

import com.example.backend.entity.Bill;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillDTO {
    
    private Long id;
    private StudentDTO student;
    private Long enrollmentId;
    private Bill.BillType billType;
    private BigDecimal amount;
    private String description;
    private LocalDate dueDate;
    private Bill.Status status;
    private PaymentDTO payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
