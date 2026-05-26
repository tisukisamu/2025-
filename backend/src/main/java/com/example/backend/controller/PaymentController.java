package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.entity.PaymentOrder;
import com.example.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public Result<PaymentOrder> createPayment(
            @RequestParam Long appointmentId,
            @RequestParam Long userId,
            @RequestParam BigDecimal amount) {
        PaymentOrder order = paymentService.createPaymentOrder(appointmentId, userId, amount);
        return Result.success("创建成功", order);
    }

    @GetMapping("/order/{orderNo}")
    public Result<PaymentOrder> getPaymentByOrderNo(@PathVariable String orderNo) {
        PaymentOrder order = paymentService.getPaymentByOrderNo(orderNo);
        return Result.success(order);
    }

    @GetMapping("/appointment/{appointmentId}")
    public Result<PaymentOrder> getPaymentByAppointmentId(@PathVariable Long appointmentId) {
        PaymentOrder order = paymentService.getPaymentByAppointmentId(appointmentId);
        return Result.success(order);
    }

    @PutMapping("/status")
    public Result<PaymentOrder> updatePaymentStatus(
            @RequestParam String orderNo,
            @RequestParam String status,
            @RequestParam(required = false) String transactionId) {
        PaymentOrder order = paymentService.updatePaymentStatus(orderNo, status, transactionId);
        return Result.success("状态更新成功", order);
    }

    @PutMapping("/refund/{orderNo}")
    public Result<PaymentOrder> refundPayment(@PathVariable String orderNo) {
        PaymentOrder order = paymentService.refundPayment(orderNo);
        return Result.success("退款成功", order);
    }
}
