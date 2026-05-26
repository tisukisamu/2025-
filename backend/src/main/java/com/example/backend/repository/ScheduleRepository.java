package com.example.backend.repository;

import com.example.backend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByCourseId(Long courseId);
    
    List<Schedule> findByTeacherId(Long teacherId);
    
    List<Schedule> findByStatus(Schedule.Status status);
    
    @Query("SELECT s FROM Schedule s WHERE s.teacher.id = :teacherId AND s.startTime >= :start AND s.endTime <= :end")
    List<Schedule> findByTeacherIdAndTimeRange(@Param("teacherId") Long teacherId, 
                                                @Param("start") LocalDateTime start, 
                                                @Param("end") LocalDateTime end);
    
    @Query("SELECT s FROM Schedule s WHERE s.startTime >= :start AND s.endTime <= :end")
    List<Schedule> findByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Schedule s " +
           "WHERE s.teacher.id = :teacherId " +
           "AND s.status != 'CANCELLED' " +
           "AND ((s.startTime <= :endTime AND s.endTime >= :startTime))")
    boolean hasConflict(@Param("teacherId") Long teacherId, 
                       @Param("startTime") LocalDateTime startTime, 
                       @Param("endTime") LocalDateTime endTime);
}
