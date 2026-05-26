package com.example.backend.repository;

import com.example.backend.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByEnrollmentId(Long enrollmentId);
    
    Optional<Payment> findByTransactionId(String transactionId);
    
    Page<Payment> findByStatus(Payment.Status status, Pageable pageable);
    
    List<Payment> findByEnrollmentStudentId(Long studentId);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'SUCCESS' AND p.paymentTime BETWEEN :start AND :end")
    BigDecimal calculateTotalIncome(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    Long countByStatus(@Param("status") Payment.Status status);
    
    @Query("SELECT p FROM Payment p WHERE p.paymentTime BETWEEN :start AND :end")
    List<Payment> findByPaymentTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
