package com.example.backend.service;

import com.example.backend.dto.request.BillRequest;
import com.example.backend.dto.request.PaymentRequest;
import com.example.backend.entity.*;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FinanceService {
    
    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    
    public Payment createPayment(PaymentRequest request) {
        Payment payment = new Payment();
        
        if (request.getBillId() != null) {
            Bill bill = billRepository.findById(request.getBillId())
                .orElseThrow(() -> new ResourceNotFoundException("账单", "id", request.getBillId()));
            
            if (bill.getStatus() == Bill.Status.PAID) {
                throw new BusinessException("该账单已支付");
            }
            
            if (bill.getEnrollmentId() != null) {
                Enrollment enrollment = enrollmentRepository.findById(bill.getEnrollmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("报名记录", "id", bill.getEnrollmentId()));
                payment.setEnrollment(enrollment);
            }
            
            payment.setAmount(bill.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setStatus(Payment.Status.PENDING);
            
            Payment savedPayment = paymentRepository.save(payment);
            
            bill.setPayment(savedPayment);
            billRepository.save(bill);
            
            return savedPayment;
        } else if (request.getEnrollmentId() != null) {
            Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
                .orElseThrow(() -> new ResourceNotFoundException("报名记录", "id", request.getEnrollmentId()));
            
            if (enrollment.getPaymentStatus() == Enrollment.PaymentStatus.PAID) {
                throw new BusinessException("该报名已支付");
            }
            
            payment.setEnrollment(enrollment);
            payment.setAmount(request.getAmount());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setTransactionId(UUID.randomUUID().toString());
            payment.setStatus(Payment.Status.PENDING);
            
            return paymentRepository.save(payment);
        } else {
            throw new BusinessException("必须提供账单ID或报名ID");
        }
    }
    
    public Payment processPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("支付记录", "id", paymentId));
        
        if (payment.getStatus() != Payment.Status.PENDING) {
            throw new BusinessException("该支付已处理");
        }
        
        payment.setStatus(Payment.Status.SUCCESS);
        payment.setPaymentTime(LocalDateTime.now());
        
        if (payment.getEnrollment() != null) {
            Enrollment enrollment = payment.getEnrollment();
            enrollment.setPaymentStatus(Enrollment.PaymentStatus.PAID);
            enrollment.setStatus(Enrollment.Status.CONFIRMED);
            enrollmentRepository.save(enrollment);
        }
        
        billRepository.findByPaymentId(paymentId).ifPresent(bill -> {
            bill.setStatus(Bill.Status.PAID);
            billRepository.save(bill);
            
            if (bill.getEnrollmentId() != null) {
                enrollmentRepository.findById(bill.getEnrollmentId()).ifPresent(enrollment -> {
                    enrollment.setPaymentStatus(Enrollment.PaymentStatus.PAID);
                    enrollment.setStatus(Enrollment.Status.CONFIRMED);
                    enrollmentRepository.save(enrollment);
                });
            }
        });
        
        return paymentRepository.save(payment);
    }
    
    public Payment refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("支付记录", "id", paymentId));
        
        if (payment.getStatus() != Payment.Status.SUCCESS) {
            throw new BusinessException("只能退款已成功的支付");
        }
        
        payment.setStatus(Payment.Status.REFUNDED);
        
        Enrollment enrollment = payment.getEnrollment();
        enrollment.setPaymentStatus(Enrollment.PaymentStatus.REFUNDED);
        enrollmentRepository.save(enrollment);
        
        return paymentRepository.save(payment);
    }
    
    public Bill createBill(BillRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("学员", "id", request.getStudentId()));
        
        Bill bill = new Bill();
        bill.setStudent(student);
        bill.setBillType(request.getBillType());
        bill.setAmount(request.getAmount());
        bill.setDescription(request.getDescription());
        bill.setDueDate(request.getDueDate());
        bill.setStatus(Bill.Status.UNPAID);
        
        return billRepository.save(bill);
    }
    
    @Transactional(readOnly = true)
    public Page<Payment> getPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Payment> getPaymentsByStatus(Payment.Status status, Pageable pageable) {
        return paymentRepository.findByStatus(status, pageable);
    }

    @Transactional(readOnly = true)
    public List<Payment> getStudentPayments(Long studentId) {
        return paymentRepository.findByEnrollmentStudentId(studentId);
    }
    
    @Transactional(readOnly = true)
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("支付记录", "id", id));
    }
    
    @Transactional(readOnly = true)
    public Page<Bill> getBills(Pageable pageable) {
        return billRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Bill> getBillsByStatus(Bill.Status status, Pageable pageable) {
        return billRepository.findByStatus(status, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Bill> getStudentBills(Long studentId) {
        return billRepository.findByStudentId(studentId);
    }
    
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalIncome(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        BigDecimal income = paymentRepository.calculateTotalIncome(startDateTime, endDateTime);
        return income != null ? income : BigDecimal.ZERO;
    }
    
    @Transactional(readOnly = true)
    public Map<String, BigDecimal> getIncomeByCategory(LocalDate start, LocalDate end) {
        Map<String, BigDecimal> incomeMap = new HashMap<>();
        
        BigDecimal totalIncome = calculateTotalIncome(start, end);
        incomeMap.put("total", totalIncome);
        
        return incomeMap;
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> getFinanceStatistics(LocalDate start, LocalDate end) {
        Map<String, Object> statistics = new HashMap<>();
        
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        BigDecimal totalIncome = calculateTotalIncome(start, end);
        statistics.put("totalIncome", totalIncome);
        
        Long successfulPayments = paymentRepository.countByStatus(Payment.Status.SUCCESS);
        Long pendingPayments = paymentRepository.countByStatus(Payment.Status.PENDING);
        Long failedPayments = paymentRepository.countByStatus(Payment.Status.FAILED);
        
        statistics.put("successfulPayments", successfulPayments);
        statistics.put("pendingPayments", pendingPayments);
        statistics.put("failedPayments", failedPayments);
        
        Long unpaidBills = billRepository.countByStatus(Bill.Status.UNPAID);
        Long paidBills = billRepository.countByStatus(Bill.Status.PAID);
        Long overdueBills = billRepository.countByStatus(Bill.Status.OVERDUE);
        
        statistics.put("unpaidBills", unpaidBills);
        statistics.put("paidBills", paidBills);
        statistics.put("overdueBills", overdueBills);
        
        return statistics;
    }
}
