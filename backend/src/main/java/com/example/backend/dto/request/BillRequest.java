package com.example.backend.dto.request;

import com.example.backend.entity.Bill;
import com.example.backend.entity.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillRequest {
    
    @NotNull(message = "学员ID不能为空")
    private Long studentId;
    
    @NotNull(message = "账单类型不能为空")
    private Bill.BillType billType;
    
    @NotNull(message = "账单金额不能为空")
    @DecimalMin(value = "0.0", message = "金额不能为负数")
    private BigDecimal amount;
    
    private String description;
    
    private LocalDate dueDate;
}
