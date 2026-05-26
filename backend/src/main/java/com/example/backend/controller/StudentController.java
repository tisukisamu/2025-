package com.example.backend.controller;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.request.StudentRequest;
import com.example.backend.dto.response.StudentDTO;
import com.example.backend.entity.Course;
import com.example.backend.entity.Enrollment;
import com.example.backend.entity.Student;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {
    
    private final StudentService studentService;
    private final CourseService courseService;
    private final UserService userService;
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Map<String, Object>> getStudents(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Student.Status status) {
        
        Page<Student> students;
        if (name != null && !name.isEmpty()) {
            students = studentService.searchStudents(name, pageable);
        } else if (status != null) {
            students = studentService.getStudentsByStatus(status, pageable);
        } else {
            students = studentService.getStudents(pageable);
        }
        
        Page<StudentDTO> studentDTOs = students.map(this::convertToDTO);
        
        PageResponse<StudentDTO> pageResponse = new PageResponse<>(
            studentDTOs.getContent(),
            studentDTOs.getTotalElements(),
            studentDTOs.getTotalPages(),
            studentDTOs.getSize(),
            studentDTOs.getNumber(),
            studentDTOs.isFirst(),
            studentDTOs.isLast(),
            studentDTOs.isEmpty()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", pageResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", convertToDTO(student));
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createStudent(@RequestBody StudentRequest request) {
        Student student = studentService.createStudent(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", convertToDTO(student));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable Long id, @RequestBody StudentRequest request) {
        Student student = studentService.updateStudent(id, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", convertToDTO(student));
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/enrollments")
    public ResponseEntity<?> getStudentEnrollments(@PathVariable Long id) {
        List<Enrollment> enrollments = courseService.getStudentEnrollments(id);
        return ResponseEntity.ok(enrollments);
    }
    
    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getStudentCourses(@PathVariable Long id) {
        List<Enrollment> enrollments = courseService.getStudentEnrollments(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", enrollments.stream().map(this::convertEnrollmentCourse).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/courses")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyCourses(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Student student = studentService.getStudentByUserId(userId);
        List<Enrollment> enrollments = courseService.getStudentEnrollments(student.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", enrollments.stream().map(this::convertEnrollmentCourse).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/enrollments/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long enrollmentId, 
                                                 @RequestParam(required = false) String reason) {
        courseService.cancelEnrollment(enrollmentId, reason);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<Page<StudentDTO>> getStudentsByStatus(@PathVariable Student.Status status, 
                                                                 Pageable pageable) {
        Page<Student> students = studentService.getStudentsByStatus(status, pageable);
        Page<StudentDTO> studentDTOs = students.map(this::convertToDTO);
        return ResponseEntity.ok(studentDTOs);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<StudentDTO>> getActiveStudents() {
        List<Student> students = studentService.getActiveStudents();
        List<StudentDTO> studentDTOs = students.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<List<StudentDTO>> searchStudents(@RequestParam String name) {
        List<Student> students = studentService.searchStudents(name);
        List<StudentDTO> studentDTOs = students.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(studentDTOs);
    }
    
    private StudentDTO convertToDTO(Student student) {
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

    private Map<String, Object> convertEnrollmentCourse(Enrollment enrollment) {
        Map<String, Object> item = new HashMap<>();
        item.put("enrollmentId", enrollment.getId());
        item.put("enrollmentDate", enrollment.getEnrollmentDate());
        item.put("status", enrollment.getStatus());
        item.put("paymentStatus", enrollment.getPaymentStatus());
        item.put("course", convertCourseMap(enrollment.getCourse()));
        return item;
    }

    private Map<String, Object> convertCourseMap(Course course) {
        Map<String, Object> courseMap = new HashMap<>();
        courseMap.put("id", course.getId());
        courseMap.put("name", course.getName());
        courseMap.put("description", course.getDescription());
        courseMap.put("category", course.getCategory());
        courseMap.put("level", course.getLevel());
        courseMap.put("duration", course.getDuration());
        courseMap.put("price", course.getPrice());
        courseMap.put("capacity", course.getCapacity());
        courseMap.put("enrolledCount", course.getEnrolledCount());
        courseMap.put("image", course.getImage());
        courseMap.put("createdAt", course.getCreatedAt());
        courseMap.put("updatedAt", course.getUpdatedAt());
        courseMap.put("teacherName", course.getTeacher() != null ? course.getTeacher().getName() : "");
        return courseMap;
    }

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof com.example.backend.security.UserDetailsImpl) {
            return ((com.example.backend.security.UserDetailsImpl) userDetails).getId();
        }
        return userService.findByUsername(userDetails.getUsername()).getId();
    }
}
