package com.example.backend.repository;

import com.example.backend.entity.Teacher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    
    Optional<Teacher> findByUserId(Long userId);
    
    Page<Teacher> findByStatus(Teacher.Status status, Pageable pageable);
    
    List<Teacher> findByStatus(Teacher.Status status);
    
    List<Teacher> findByNameContaining(String name);
    
    Page<Teacher> findByNameContaining(String name, Pageable pageable);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
}
