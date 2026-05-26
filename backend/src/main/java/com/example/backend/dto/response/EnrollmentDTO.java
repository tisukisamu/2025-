package com.example.backend.dto.response;

import com.example.backend.entity.Enrollment;
import com.example.backend.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    
    private Long id;
    private StudentDTO student;
    private CourseDTO course;
    private ScheduleDTO schedule;
    private LocalDateTime enrollmentDate;
    private Enrollment.Status status;
    private Enrollment.PaymentStatus paymentStatus;
    private String cancelReason;
    private LocalDateTime cancelledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long billId;
}
