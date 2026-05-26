package com.example.backend.repository;

import com.example.backend.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    Optional<Appointment> findByOrderNo(String orderNo);
    
    Page<Appointment> findByUserIdAndDeletedOrderByCreatedAtDesc(Long userId, Integer deleted, Pageable pageable);
    
    Page<Appointment> findByStatusAndDeleted(String status, Integer deleted, Pageable pageable);
    
    Page<Appointment> findByUserIdAndStatusAndDeleted(Long userId, String status, Integer deleted, Pageable pageable);
    
    Page<Appointment> findByDeletedOrderByCreatedAtDesc(Integer deleted, Pageable pageable);
    
    Page<Appointment> findByStatus(String status, Pageable pageable);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
