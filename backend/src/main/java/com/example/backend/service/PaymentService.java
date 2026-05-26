package com.example.backend.service;

import com.example.backend.entity.PaymentOrder;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentOrder createPaymentOrder(Long appointmentId, Long userId, BigDecimal amount) {
        if (paymentRepository.existsByAppointmentId(appointmentId)) {
            throw new BusinessException("该预约已存在支付订单");
        }
        
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(generatePaymentOrderNo());
        order.setAppointmentId(appointmentId);
        order.setUserId(userId);
        order.setAmount(amount);
        order.setStatus("pending");
        
        return paymentRepository.save(order);
    }

    public PaymentOrder getPaymentByOrderNo(String orderNo) {
        return paymentRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new ResourceNotFoundException("支付订单", "orderNo", orderNo));
    }

    public PaymentOrder getPaymentByAppointmentId(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("支付订单", "appointmentId", appointmentId));
    }

    @Transactional
    public PaymentOrder updatePaymentStatus(String orderNo, String status, String transactionId) {
        PaymentOrder order = getPaymentByOrderNo(orderNo);
        order.setStatus(status);
        order.setTransactionId(transactionId);
        
        if ("paid".equals(status)) {
            order.setPaidTime(LocalDateTime.now());
        }
        
        return paymentRepository.save(order);
    }

    @Transactional
    public PaymentOrder refundPayment(String orderNo) {
        PaymentOrder order = getPaymentByOrderNo(orderNo);
        
        if (!"paid".equals(order.getStatus())) {
            throw new BusinessException("当前状态不允许退款");
        }
        
        order.setStatus("refunded");
        return paymentRepository.save(order);
    }

    private String generatePaymentOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "PAY" + timestamp + uuid;
    }
}
