package com.example.backend.repository;

import com.example.backend.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Page<Course> findByStatus(Course.Status status, Pageable pageable);
    
    List<Course> findByStatus(Course.Status status);
    
    List<Course> findByTeacherId(Long teacherId);
    
    List<Course> findByNameContaining(String name);
    
    Page<Course> findByNameContaining(String name, Pageable pageable);
    
    @Query("SELECT c FROM Course c WHERE c.enrolledCount < c.capacity AND c.status = :status")
    List<Course> findAvailableCourses(@Param("status") Course.Status status);
    
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId AND e.status NOT IN ('CANCELLED')")
    Long countActiveEnrollments(@Param("courseId") Long courseId);
}
