package com.example.backend.service;

import com.example.backend.dto.request.CourseRequest;
import com.example.backend.dto.request.EnrollmentRequest;
import com.example.backend.dto.response.CourseDTO;
import com.example.backend.dto.response.EnrollmentDTO;
import com.example.backend.dto.response.ScheduleDTO;
import com.example.backend.dto.response.StudentDTO;
import com.example.backend.dto.response.TeacherDTO;
import com.example.backend.entity.*;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ScheduleRepository scheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    
    public Course createCourse(CourseRequest request) {
        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("教师", "id", request.getTeacherId()));
        }
        
        Course course = new Course();
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setPrice(request.getPrice());
        course.setCapacity(request.getCapacity());
        course.setImage(request.getImage());
        course.setStatus(request.getStatus());
        
        return courseRepository.save(course);
    }
    
    public Course updateCourse(Long id, CourseRequest request) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("课程", "id", id));
        
        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("教师", "id", request.getTeacherId()));
        }
        
        course.setName(request.getName());
        course.setDescription(request.getDescription());
        course.setTeacher(teacher);
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setDuration(request.getDuration());
        course.setPrice(request.getPrice());
        course.setCapacity(request.getCapacity());
        course.setImage(request.getImage());
        course.setStatus(request.getStatus());
        
        return courseRepository.save(course);
    }
    
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("课程", "id", id));

        List<Schedule> schedules = scheduleRepository.findByCourseId(id);
        if (!schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                List<Enrollment> scheduleEnrollments = enrollmentRepository.findByScheduleId(schedule.getId());
                if (!scheduleEnrollments.isEmpty()) {
                    for (Enrollment enrollment : scheduleEnrollments) {
                        enrollment.setSchedule(null);
                    }
                    enrollmentRepository.saveAll(scheduleEnrollments);
                }
            }
            scheduleRepository.deleteAll(schedules);
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(id);
        if (!enrollments.isEmpty()) {
            for (Enrollment enrollment : enrollments) {
                paymentRepository.findByEnrollmentId(enrollment.getId()).ifPresent(paymentRepository::delete);
            }
            enrollmentRepository.deleteAll(enrollments);
        }

        courseRepository.delete(course);
    }
    
    @Transactional(readOnly = true)
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("课程", "id", id));
    }
    
    @Transactional(readOnly = true)
    public Page<Course> getCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Course> getCoursesByStatus(Course.Status status, Pageable pageable) {
        return courseRepository.findByStatus(status, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Course> searchCourses(String name, Pageable pageable) {
        return courseRepository.findByNameContaining(name, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Course> getAvailableCourses() {
        return courseRepository.findAvailableCourses(Course.Status.PUBLISHED);
    }
    
    public Schedule createSchedule(Long courseId, Schedule schedule) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("课程", "id", courseId));
        
        if (scheduleRepository.hasConflict(schedule.getTeacher().getId(), 
                                          schedule.getStartTime(), 
                                          schedule.getEndTime())) {
            throw new BusinessException("该教师在此时间段已有排课");
        }
        
        schedule.setCourse(course);
        return scheduleRepository.save(schedule);
    }
    
    @Transactional(readOnly = true)
    public List<Schedule> getCourseSchedules(Long courseId) {
        return scheduleRepository.findByCourseId(courseId);
    }
    
    public EnrollmentDTO enrollCourse(Long courseId, Long studentId, EnrollmentRequest request) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("课程", "id", courseId));
        
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("学员", "id", studentId));
        
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndStatusNot(
                studentId, courseId, Enrollment.Status.CANCELLED)) {
            throw new BusinessException("您已报名该课程");
        }
        
        Long activeEnrollments = enrollmentRepository.countActiveEnrollmentsByCourse(courseId);
        if (activeEnrollments >= course.getCapacity()) {
            throw new BusinessException("课程已满员");
        }
        
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setStatus(Enrollment.Status.PENDING);
        enrollment.setPaymentStatus(Enrollment.PaymentStatus.UNPAID);
        
        if (request.getScheduleId() != null) {
            Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("排课", "id", request.getScheduleId()));
            enrollment.setSchedule(schedule);
        }
        
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        
        Bill bill = new Bill();
        bill.setStudent(student);
        bill.setEnrollmentId(savedEnrollment.getId());
        bill.setBillType(Bill.BillType.TUITION);
        bill.setAmount(course.getPrice());
        bill.setDescription("课程报名：" + course.getName());
        bill.setStatus(Bill.Status.UNPAID);
        Bill savedBill = billRepository.save(bill);
        
        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseRepository.save(course);
        
        return EnrollmentDTO.builder()
            .id(savedEnrollment.getId())
            .student(convertStudentToDTO(student))
            .course(convertCourseToDTO(course))
            .schedule(savedEnrollment.getSchedule() != null ? convertScheduleToDTO(savedEnrollment.getSchedule()) : null)
            .enrollmentDate(savedEnrollment.getEnrollmentDate())
            .status(savedEnrollment.getStatus())
            .paymentStatus(savedEnrollment.getPaymentStatus())
            .cancelReason(savedEnrollment.getCancelReason())
            .cancelledAt(savedEnrollment.getCancelledAt())
            .createdAt(savedEnrollment.getCreatedAt())
            .updatedAt(savedEnrollment.getUpdatedAt())
            .billId(savedBill.getId())
            .build();
    }
    
    public void cancelEnrollment(Long enrollmentId, String reason) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new ResourceNotFoundException("报名记录", "id", enrollmentId));
        
        if (enrollment.getStatus() == Enrollment.Status.CANCELLED) {
            throw new BusinessException("该报名已取消");
        }
        
        enrollment.setStatus(Enrollment.Status.CANCELLED);
        enrollment.setCancelReason(reason);
        enrollmentRepository.save(enrollment);
        
        Course course = enrollment.getCourse();
        course.setEnrolledCount(Math.max(0, course.getEnrolledCount() - 1));
        courseRepository.save(course);
    }
    
    @Transactional(readOnly = true)
    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findActiveEnrollmentsByStudent(studentId);
    }
    
    @Transactional(readOnly = true)
    public List<Course> getTeacherCourses(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
    
    private StudentDTO convertStudentToDTO(Student student) {
        return StudentDTO.builder()
            .id(student.getId())
            .userId(student.getUser() != null ? student.getUser().getId() : null)
            .name(student.getName())
            .phone(student.getPhone())
            .email(student.getEmail())
            .gender(student.getGender())
            .birthDate(student.getBirthDate())
            .emergencyContact(student.getEmergencyContact())
            .emergencyPhone(student.getEmergencyPhone())
            .avatar(student.getAvatar())
            .status(student.getStatus())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }
    
    private CourseDTO convertCourseToDTO(Course course) {
        return CourseDTO.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .teacher(course.getTeacher() != null ? convertTeacherToDTO(course.getTeacher()) : null)
            .category(course.getCategory())
            .level(course.getLevel())
            .duration(course.getDuration())
            .price(course.getPrice())
            .capacity(course.getCapacity())
            .enrolledCount(course.getEnrolledCount())
            .image(course.getImage())
            .status(course.getStatus())
            .createdAt(course.getCreatedAt())
            .updatedAt(course.getUpdatedAt())
            .build();
    }
    
    private TeacherDTO convertTeacherToDTO(Teacher teacher) {
        return TeacherDTO.builder()
            .id(teacher.getId())
            .userId(teacher.getUser() != null ? teacher.getUser().getId() : null)
            .name(teacher.getName())
            .phone(teacher.getPhone())
            .email(teacher.getEmail())
            .subjects(teacher.getSubjects())
            .experience(teacher.getExperience())
            .bio(teacher.getBio())
            .avatar(teacher.getAvatar())
            .status(teacher.getStatus())
            .createdAt(teacher.getCreatedAt())
            .updatedAt(teacher.getUpdatedAt())
            .build();
    }
    
    private ScheduleDTO convertScheduleToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
            .id(schedule.getId())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .location(schedule.getLocation())
            .room(schedule.getRoom())
            .status(schedule.getStatus())
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
    }
}
