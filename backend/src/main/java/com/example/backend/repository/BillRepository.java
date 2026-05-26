package com.example.backend.repository;

import com.example.backend.entity.Bill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    
    List<Bill> findByStudentId(Long studentId);
    
    Page<Bill> findByStudentId(Long studentId, Pageable pageable);
    
    Page<Bill> findByStatus(Bill.Status status, Pageable pageable);
    
    List<Bill> findByStatus(Bill.Status status);
    
    Optional<Bill> findByPaymentId(Long paymentId);
    
    @Query("SELECT SUM(b.amount) FROM Bill b WHERE b.status = :status")
    BigDecimal calculateTotalAmountByStatus(@Param("status") Bill.Status status);
    
    @Query("SELECT COUNT(b) FROM Bill b WHERE b.status = :status")
    Long countByStatus(@Param("status") Bill.Status status);
}
