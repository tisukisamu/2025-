package com.example.backend.service;

import com.example.backend.dto.request.StudentRequest;
import com.example.backend.entity.Student;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.StudentRepository;
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
public class StudentService {
    
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    
    public Student createStudent(StudentRequest request) {
        if (request.getPhone() != null && studentRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("手机号已被使用");
        }
        
        if (request.getEmail() != null && studentRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }
        
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", request.getUserId()));
        }
        
        Student student = new Student();
        student.setUser(user);
        student.setName(request.getName());
        student.setPhone(request.getPhone());
        student.setEmail(request.getEmail());
        student.setGender(request.getGender());
        student.setBirthDate(request.getBirthDate());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setEmergencyPhone(request.getEmergencyPhone());
        student.setAvatar(request.getAvatar());
        student.setStatus(request.getStatus());
        
        return studentRepository.save(student);
    }
    
    public Student updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("学员", "id", id));
        
        if (request.getPhone() != null && !request.getPhone().equals(student.getPhone())) {
            if (studentRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被使用");
            }
        }
        
        if (request.getEmail() != null && !request.getEmail().equals(student.getEmail())) {
            if (studentRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException("邮箱已被使用");
            }
        }
        
        User user = null;
        if (request.getUserId() != null) {
            user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", request.getUserId()));
        }
        
        student.setUser(user);
        student.setName(request.getName());
        student.setPhone(request.getPhone());
        student.setEmail(request.getEmail());
        student.setGender(request.getGender());
        student.setBirthDate(request.getBirthDate());
        student.setEmergencyContact(request.getEmergencyContact());
        student.setEmergencyPhone(request.getEmergencyPhone());
        student.setAvatar(request.getAvatar());
        student.setStatus(request.getStatus());
        
        return studentRepository.save(student);
    }
    
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("学员", "id", id));
        studentRepository.delete(student);
    }
    
    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("学员", "id", id));
    }
    
    @Transactional(readOnly = true)
    public Page<Student> getStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<Student> getStudentsByStatus(Student.Status status, Pageable pageable) {
        return studentRepository.findByStatus(status, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<Student> getActiveStudents() {
        return studentRepository.findByStatus(Student.Status.ACTIVE);
    }
    
    @Transactional(readOnly = true)
    public List<Student> searchStudents(String name) {
        return studentRepository.findByNameContaining(name);
    }
    
    @Transactional(readOnly = true)
    public Page<Student> searchStudents(String name, Pageable pageable) {
        return studentRepository.findByNameContaining(name, pageable);
    }
    
    @Transactional
    public Student getStudentByUserId(Long userId) {
        return studentRepository.findByUserId(userId)
            .orElseGet(() -> {
                User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userId));
                Student student = new Student();
                student.setUser(user);
                student.setName(user.getRealName() != null && !user.getRealName().isBlank() ? user.getRealName() : user.getUsername());
                student.setPhone(user.getPhone());
                student.setEmail(user.getEmail());
                student.setAvatar(user.getAvatar());
                student.setStatus(user.getStatus() == User.Status.INACTIVE ? Student.Status.INACTIVE : Student.Status.ACTIVE);
                return studentRepository.save(student);
            });
    }
}
