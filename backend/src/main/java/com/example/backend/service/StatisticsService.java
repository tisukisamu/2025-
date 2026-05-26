package com.example.backend.service;

import com.example.backend.dto.response.StatisticsDTO;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {
    
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    
    public StatisticsDTO.CourseStatistics getCourseStatistics() {
        List<Course> allCourses = courseRepository.findAll();
        
        long totalCourses = allCourses.size();
        long publishedCourses = allCourses.stream()
            .filter(c -> c.getStatus() == Course.Status.PUBLISHED)
            .count();
        long draftCourses = allCourses.stream()
            .filter(c -> c.getStatus() == Course.Status.DRAFT)
            .count();
        long closedCourses = allCourses.stream()
            .filter(c -> c.getStatus() == Course.Status.CLOSED)
            .count();
        
        long totalEnrollments = enrollmentRepository.count();
        
        Map<String, Long> enrollmentsByCourse = allCourses.stream()
            .collect(Collectors.toMap(
                Course::getName,
                course -> enrollmentRepository.countActiveEnrollmentsByCourse(course.getId())
            ));
        
        return StatisticsDTO.CourseStatistics.builder()
            .totalCourses(totalCourses)
            .publishedCourses(publishedCourses)
            .draftCourses(draftCourses)
            .closedCourses(closedCourses)
            .totalEnrollments(totalEnrollments)
            .enrollmentsByCourse(enrollmentsByCourse)
            .build();
    }
    
    public StatisticsDTO.FinanceStatistics getFinanceStatistics(LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(LocalTime.MAX);
        
        BigDecimal totalIncome = paymentRepository.calculateTotalIncome(startDateTime, endDateTime);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }
        
        BigDecimal totalPending = BigDecimal.ZERO;
        BigDecimal totalRefunded = BigDecimal.ZERO;
        
        long totalPayments = paymentRepository.count();
        long successfulPayments = paymentRepository.countByStatus(Payment.Status.SUCCESS);
        long pendingPayments = paymentRepository.countByStatus(Payment.Status.PENDING);
        long failedPayments = paymentRepository.countByStatus(Payment.Status.FAILED);
        
        Map<String, BigDecimal> incomeByMonth = new HashMap<>();
        
        return StatisticsDTO.FinanceStatistics.builder()
            .totalIncome(totalIncome)
            .totalPending(totalPending)
            .totalRefunded(totalRefunded)
            .totalPayments(totalPayments)
            .successfulPayments(successfulPayments)
            .pendingPayments(pendingPayments)
            .failedPayments(failedPayments)
            .incomeByMonth(incomeByMonth)
            .build();
    }
    
    public StatisticsDTO.StudentStatistics getStudentStatistics() {
        List<Student> allStudents = studentRepository.findAll();
        
        long totalStudents = allStudents.size();
        long activeStudents = allStudents.stream()
            .filter(s -> s.getStatus() == Student.Status.ACTIVE)
            .count();
        long inactiveStudents = allStudents.stream()
            .filter(s -> s.getStatus() == Student.Status.INACTIVE)
            .count();
        
        Map<String, Long> enrollmentsByStatus = new HashMap<>();
        enrollmentsByStatus.put("PENDING", enrollmentRepository.findAll().stream()
            .filter(e -> e.getStatus() == Enrollment.Status.PENDING)
            .count());
        enrollmentsByStatus.put("CONFIRMED", enrollmentRepository.findAll().stream()
            .filter(e -> e.getStatus() == Enrollment.Status.CONFIRMED)
            .count());
        enrollmentsByStatus.put("CANCELLED", enrollmentRepository.findAll().stream()
            .filter(e -> e.getStatus() == Enrollment.Status.CANCELLED)
            .count());
        enrollmentsByStatus.put("COMPLETED", enrollmentRepository.findAll().stream()
            .filter(e -> e.getStatus() == Enrollment.Status.COMPLETED)
            .count());
        
        Map<String, Long> studentsByGender = new HashMap<>();
        studentsByGender.put("MALE", allStudents.stream()
            .filter(s -> s.getGender() == Student.Gender.MALE)
            .count());
        studentsByGender.put("FEMALE", allStudents.stream()
            .filter(s -> s.getGender() == Student.Gender.FEMALE)
            .count());
        studentsByGender.put("OTHER", allStudents.stream()
            .filter(s -> s.getGender() == Student.Gender.OTHER)
            .count());
        
        return StatisticsDTO.StudentStatistics.builder()
            .totalStudents(totalStudents)
            .activeStudents(activeStudents)
            .inactiveStudents(inactiveStudents)
            .enrollmentsByStatus(enrollmentsByStatus)
            .studentsByGender(studentsByGender)
            .build();
    }
    
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> dashboard = new HashMap<>();
        
        dashboard.put("totalCourses", courseRepository.count());
        dashboard.put("totalStudents", studentRepository.count());
        dashboard.put("totalTeachers", teacherRepository.count());
        dashboard.put("totalEnrollments", enrollmentRepository.count());
        
        dashboard.put("activeStudents", studentRepository.findByStatus(Student.Status.ACTIVE).size());
        dashboard.put("activeTeachers", teacherRepository.findByStatus(Teacher.Status.ACTIVE).size());
        dashboard.put("publishedCourses", courseRepository.findByStatus(Course.Status.PUBLISHED).size());
        
        return dashboard;
    }
}
