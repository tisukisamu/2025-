package com.example.backend.repository;

import com.example.backend.entity.ProcessStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcessRepository extends JpaRepository<ProcessStage, Long> {
    
    List<ProcessStage> findByAppointmentIdOrderByCreatedAtAsc(Long appointmentId);
    
    Optional<ProcessStage> findByAppointmentIdAndStage(Long appointmentId, String stage);
    
    boolean existsByAppointmentIdAndStage(Long appointmentId, String stage);
}
