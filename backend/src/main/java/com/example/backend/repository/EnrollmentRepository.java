package com.example.backend.repository;

import com.example.backend.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    
    List<Enrollment> findByStudentId(Long studentId);
    
    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByScheduleId(Long scheduleId);
    
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);
    
    Page<Enrollment> findByCourseId(Long courseId, Pageable pageable);
    
    @Query("SELECT e FROM Enrollment e WHERE e.student.id = :studentId AND e.status NOT IN ('CANCELLED')")
    List<Enrollment> findActiveEnrollmentsByStudent(@Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status NOT IN ('CANCELLED')")
    Long countActiveEnrollmentsByCourse(@Param("courseId") Long courseId);
    
    boolean existsByStudentIdAndCourseIdAndStatusNot(Long studentId, Long courseId, Enrollment.Status status);
}
