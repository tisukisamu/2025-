package com.example.backend.controller;

import com.example.backend.dto.PageResponse;
import com.example.backend.dto.request.TeacherRequest;
import com.example.backend.dto.response.CourseDTO;
import com.example.backend.dto.response.TeacherDTO;
import com.example.backend.entity.Course;
import com.example.backend.entity.Teacher;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.CourseService;
import com.example.backend.service.TeacherService;
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
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {
    
    private final TeacherService teacherService;
    private final CourseService courseService;
    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTeachers(
            Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Teacher.Status status) {
        
        Page<Teacher> teachers;
        if (name != null && !name.isEmpty()) {
            teachers = teacherService.searchTeachers(name, pageable);
        } else if (status != null) {
            teachers = teacherService.getTeachersByStatus(status, pageable);
        } else {
            teachers = teacherService.getTeachers(pageable);
        }
        
        Page<TeacherDTO> teacherDTOs = teachers.map(this::convertToDTO);
        
        PageResponse<TeacherDTO> pageResponse = new PageResponse<>(
            teacherDTOs.getContent(),
            teacherDTOs.getTotalElements(),
            teacherDTOs.getTotalPages(),
            teacherDTOs.getSize(),
            teacherDTOs.getNumber(),
            teacherDTOs.isFirst(),
            teacherDTOs.isLast(),
            teacherDTOs.isEmpty()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", pageResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeacherById(@PathVariable Long id) {
        Teacher teacher = teacherService.getTeacherById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", convertToDTO(teacher));
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTeacher(@RequestBody TeacherRequest request) {
        Teacher teacher = teacherService.createTeacher(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", convertToDTO(teacher));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id, @RequestBody TeacherRequest request) {
        Teacher teacher = teacherService.updateTeacher(id, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", convertToDTO(teacher));
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/courses")
    public ResponseEntity<Map<String, Object>> getTeacherCourses(@PathVariable Long id) {
        List<Course> courses = courseService.getTeacherCourses(id);
        List<CourseDTO> courseDTOs = courses.stream()
            .map(this::convertCourseToDTO)
            .collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", courseDTOs);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TeacherDTO>> getTeachersByStatus(@PathVariable Teacher.Status status, 
                                                                 Pageable pageable) {
        Page<Teacher> teachers = teacherService.getTeachersByStatus(status, pageable);
        Page<TeacherDTO> teacherDTOs = teachers.map(this::convertToDTO);
        return ResponseEntity.ok(teacherDTOs);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<TeacherDTO>> getActiveTeachers() {
        List<Teacher> teachers = teacherService.getActiveTeachers();
        List<TeacherDTO> teacherDTOs = teachers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(teacherDTOs);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TeacherDTO>> searchTeachers(@RequestParam String name) {
        List<Teacher> teachers = teacherService.searchTeachers(name);
        List<TeacherDTO> teacherDTOs = teachers.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(teacherDTOs);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<Map<String, Object>> getCurrentTeacher(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = getUserIdFromUserDetails(userDetails);
        Teacher teacher = teacherService.getTeacherByUserId(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", convertToDTO(teacher));
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) userDetails).getId();
        }
        return userService.findByUsername(userDetails.getUsername()).getId();
    }
    
    private TeacherDTO convertToDTO(Teacher teacher) {
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
    
    private CourseDTO convertCourseToDTO(Course course) {
        return CourseDTO.builder()
            .id(course.getId())
            .name(course.getName())
            .description(course.getDescription())
            .teacher(course.getTeacher() != null ? convertToDTO(course.getTeacher()) : null)
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
}
