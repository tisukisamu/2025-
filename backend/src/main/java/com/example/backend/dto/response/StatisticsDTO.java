package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDTO {
    
    private CourseStatistics courseStatistics;
    private FinanceStatistics financeStatistics;
    private StudentStatistics studentStatistics;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseStatistics {
        private Long totalCourses;
        private Long publishedCourses;
        private Long draftCourses;
        private Long closedCourses;
        private Long totalEnrollments;
        private Map<String, Long> enrollmentsByCourse;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FinanceStatistics {
        private BigDecimal totalIncome;
        private BigDecimal totalPending;
        private BigDecimal totalRefunded;
        private Long totalPayments;
        private Long successfulPayments;
        private Long pendingPayments;
        private Long failedPayments;
        private Map<String, BigDecimal> incomeByMonth;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentStatistics {
        private Long totalStudents;
        private Long activeStudents;
        private Long inactiveStudents;
        private Map<String, Long> enrollmentsByStatus;
        private Map<String, Long> studentsByGender;
    }
}
