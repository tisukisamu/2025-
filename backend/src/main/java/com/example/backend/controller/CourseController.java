package com.example.backend.controller;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.request.CourseRequest;
import com.example.backend.dto.request.EnrollmentRequest;
import com.example.backend.dto.response.CourseDTO;
import com.example.backend.dto.response.EnrollmentDTO;
import com.example.backend.dto.response.ScheduleDTO;
import com.example.backend.dto.response.TeacherDTO;
import com.example.backend.entity.Course;
import com.example.backend.entity.Enrollment;
import com.example.backend.entity.Schedule;
import com.example.backend.service.CourseService;
import com.example.backend.service.StudentService;
import com.example.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    
    private final CourseService courseService;
    private final StudentService studentService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCourses(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Course.Status status) {
        
        Page<Course> courses;
        if (name != null && !name.isEmpty()) {
            courses = courseService.searchCourses(name, pageable);
        } else if (status != null) {
            courses = courseService.getCoursesByStatus(status, pageable);
        } else {
            courses = courseService.getCourses(pageable);
        }
        
        Page<CourseDTO> courseDTOs = courses.map(this::convertToDTO);
        
        PageResponse<CourseDTO> pageResponse = new PageResponse<>(
            courseDTOs.getContent(),
            courseDTOs.getTotalElements(),
            courseDTOs.getTotalPages(),
            courseDTOs.getSize(),
            courseDTOs.getNumber(),
            courseDTOs.isFirst(),
            courseDTOs.isLast(),
            courseDTOs.isEmpty()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", pageResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(convertToDTO(course));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> createCourse(@RequestBody CourseRequest request) {
        Course course = courseService.createCourse(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", convertToDTO(course));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id, @RequestBody CourseRequest request) {
        Course course = courseService.updateCourse(id, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", convertToDTO(course));
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getCourseSchedules(@PathVariable Long id) {
        List<Schedule> schedules = courseService.getCourseSchedules(id);
        List<ScheduleDTO> scheduleDTOs = schedules.stream()
            .map(this::convertScheduleToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(scheduleDTOs);
    }
    
    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> enrollCourse(@PathVariable Long id,
                                                            @RequestBody EnrollmentRequest request,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Long studentId = studentService.getStudentByUserId(userId).getId();
        request.setCourseId(id);
        EnrollmentDTO enrollmentDTO = courseService.enrollCourse(id, studentId, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "报名成功");
        response.put("data", enrollmentDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available")
    public ResponseEntity<List<CourseDTO>> getAvailableCourses() {
        List<Course> courses = courseService.getAvailableCourses();
        List<CourseDTO> courseDTOs = courses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(courseDTOs);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<CourseDTO>> getCoursesByStatus(@PathVariable Course.Status status, 
                                                               Pageable pageable) {
        Page<Course> courses = courseService.getCoursesByStatus(status, pageable);
        Page<CourseDTO> courseDTOs = courses.map(this::convertToDTO);
        return ResponseEntity.ok(courseDTOs);
    }
    
    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .teacher(convertTeacherToDTO(course))
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

    private TeacherDTO convertTeacherToDTO(Course course) {
        if (course.getTeacher() == null) {
            return null;
        }
        return TeacherDTO.builder()
            .id(course.getTeacher().getId())
            .userId(course.getTeacher().getUser() != null ? course.getTeacher().getUser().getId() : null)
            .name(course.getTeacher().getName())
            .phone(course.getTeacher().getPhone())
            .email(course.getTeacher().getEmail())
            .subjects(course.getTeacher().getSubjects())
            .experience(course.getTeacher().getExperience())
            .bio(course.getTeacher().getBio())
            .avatar(course.getTeacher().getAvatar())
            .status(course.getTeacher().getStatus())
            .createdAt(course.getTeacher().getCreatedAt())
            .updatedAt(course.getTeacher().getUpdatedAt())
            .build();
    }
    
    private ScheduleDTO convertScheduleToDTO(Schedule schedule) {
        return ScheduleDTO.builder()
            .id(schedule.getId())
            .startTime(schedule.getStartTime())
            .endTime(schedule.getEndTime())
            .location(schedule.getLocation())
            .room(schedule.getRoom())
            .isRecurring(schedule.getIsRecurring())
            .recurrencePattern(schedule.getRecurrencePattern())
            .status(schedule.getStatus())
            .createdAt(schedule.getCreatedAt())
            .updatedAt(schedule.getUpdatedAt())
            .build();
    }
    
    private EnrollmentDTO convertEnrollmentToDTO(Enrollment enrollment) {
        return EnrollmentDTO.builder()
            .id(enrollment.getId())
            .enrollmentDate(enrollment.getEnrollmentDate())
            .status(enrollment.getStatus())
            .paymentStatus(enrollment.getPaymentStatus())
            .cancelReason(enrollment.getCancelReason())
            .cancelledAt(enrollment.getCancelledAt())
            .createdAt(enrollment.getCreatedAt())
            .updatedAt(enrollment.getUpdatedAt())
            .build();
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof com.example.backend.security.UserDetailsImpl) {
            return ((com.example.backend.security.UserDetailsImpl) userDetails).getId();
        }
        return userService.findByUsername(userDetails.getUsername()).getId();
    }
}
