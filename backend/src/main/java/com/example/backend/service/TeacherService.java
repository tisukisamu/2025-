package com.example.backend.service;

import com.example.backend.dto.request.TeacherRequest;
import com.example.backend.entity.Course;
import com.example.backend.entity.Enrollment;
import com.example.backend.entity.Schedule;
import com.example.backend.entity.Teacher;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CourseRepository;
import com.example.backend.repository.EnrollmentRepository;
import com.example.backend.repository.ScheduleRepository;
import com.example.backend.repository.TeacherRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherService {
    
    private final TeacherRepository teacherRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ScheduleRepository scheduleRepository;
    private final EnrollmentRepository enrollmentRepository;
    
    public Teacher createTeacher(TeacherRequest request) {
        if (request.getPhone() != null && teacherRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被使用");
        }
        
        if (request.getEmail() != null && teacherRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }
        
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", request.getUserId()));
        }
        
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setName(request.getName());
        teacher.setPhone(request.getPhone());
        teacher.setEmail(request.getEmail());
        teacher.setSubjects(request.getSubjects());
        teacher.setExperience(request.getExperience());
        teacher.setBio(request.getBio());
        teacher.setAvatar(request.getAvatar());
        teacher.setStatus(request.getStatus());
        
        return teacherRepository.save(teacher);
    }
    
    public Teacher updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("教师", "id", id));
        
        if (request.getPhone() != null && !request.getPhone().equals(teacher.getPhone())) {
            if (teacherRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被使用");
            }
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(teacher.getEmail())) {
            if (teacherRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
        }
        
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", request.getUserId()));
        }
        
        teacher.setUser(user);
        teacher.setName(request.getName());
        teacher.setPhone(request.getPhone());
        teacher.setEmail(request.getEmail());
        teacher.setSubjects(request.getSubjects());
        teacher.setExperience(request.getExperience());
        teacher.setBio(request.getBio());
        teacher.setAvatar(request.getAvatar());
        teacher.setStatus(request.getStatus());
        
        return teacherRepository.save(teacher);
    }
    
    public void deleteTeacher(Long id) {
        Teacher teacher = teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("教师", "id", id));

        List<Course> teacherCourses = courseRepository.findByTeacherId(id);
        if (!teacherCourses.isEmpty()) {
            for (Course course : teacherCourses) {
                course.setTeacher(null);
            }
            courseRepository.saveAll(teacherCourses);
        }

        List<Schedule> teacherSchedules = scheduleRepository.findByTeacherId(id);
        if (!teacherSchedules.isEmpty()) {
            for (Schedule schedule : teacherSchedules) {
                List<Enrollment> enrollments = enrollmentRepository.findByScheduleId(schedule.getId());
                if (!enrollments.isEmpty()) {
                    for (Enrollment enrollment : enrollments) {
                        enrollment.setSchedule(null);
                    }
                    enrollmentRepository.saveAll(enrollments);
                }
            }
            scheduleRepository.deleteAll(teacherSchedules);
        }

        teacherRepository.delete(teacher);
    }
    
    @Transactional(readOnly = true)
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("教师", "id", id));
    }
    
    @Transactional(readOnly = true)
    public Page<Teacher> getTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Teacher> getTeachersByStatus(Teacher.Status status, Pageable pageable) {
        return teacherRepository.findByStatus(status, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Teacher> getActiveTeachers() {
        return teacherRepository.findByStatus(Teacher.Status.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public List<Teacher> searchTeachers(String name) {
        return teacherRepository.findByNameContaining(name);
    }
    
    @Transactional(readOnly = true)
    public Page<Teacher> searchTeachers(String name, Pageable pageable) {
        return teacherRepository.findByNameContaining(name, pageable);
    }
    
    public Teacher getTeacherByUserId(Long userId) {
        return teacherRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
                
                if (user.getRole() == User.Role.TEACHER) {
                    Teacher teacher = new Teacher();
                    teacher.setUser(user);
                    teacher.setName(user.getRealName());
                    teacher.setPhone(user.getPhone());
                    teacher.setEmail(user.getEmail());
                    teacher.setStatus(Teacher.Status.ACTIVE);
                    return teacherRepository.save(teacher);
                }
                
                throw new ResourceNotFoundException("教师", "userId", userId);
            });
    }
}
