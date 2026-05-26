package com.example.backend.repository;

import com.example.backend.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentOrder, Long> {
    
    Optional<PaymentOrder> findByOrderNo(String orderNo);
    
    Optional<PaymentOrder> findByAppointmentId(Long appointmentId);
    
    boolean existsByAppointmentId(Long appointmentId);
}
