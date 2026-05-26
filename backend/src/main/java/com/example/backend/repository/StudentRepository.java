package com.example.backend.repository;

import com.example.backend.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    
    Optional<Student> findByUserId(Long userId);
    
    Page<Student> findByStatus(Student.Status status, Pageable pageable);
    
    List<Student> findByStatus(Student.Status status);
    
    List<Student> findByNameContaining(String name);
    
    Page<Student> findByNameContaining(String name, Pageable pageable);
    
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
}
