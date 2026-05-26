package com.example.backend.controller;

import com.example.backend.dto.PageResponse;
import com.example.backend.entity.User;
import com.example.backend.service.FileUploadService;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final FileUploadService fileUploadService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status) {
        
        Sort sort = sortDir.equalsIgnoreCase("asc") 
            ? Sort.by(sortBy).ascending() 
            : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<User> userPage;
        if (username != null && !username.isEmpty()) {
            userPage = userService.searchUsers(username, pageable);
        } else if (role != null && !role.isEmpty()) {
            userPage = userService.findByRole(User.Role.valueOf(role), pageable);
        } else if (status != null && !status.isEmpty()) {
            userPage = userService.findByStatus(User.Status.valueOf(status), pageable);
        } else {
            userPage = userService.findAll(pageable);
        }
        
        PageResponse<User> pageResponse = new PageResponse<>(
            userPage.getContent(),
            userPage.getTotalElements(),
            userPage.getTotalPages(),
            userPage.getSize(),
            userPage.getNumber(),
            userPage.isFirst(),
            userPage.isLast(),
            userPage.isEmpty()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", pageResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.create(user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/avatar")
    public ResponseEntity<Map<String, Object>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String avatarUrl = fileUploadService.uploadImage(file, "avatars");
        
        Long userId = getUserIdFromUserDetails(userDetails);
        User updatedUser = userService.updateAvatar(userId, avatarUrl);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "头像上传成功");
        response.put("data", Map.of("url", avatarUrl, "user", updatedUser));
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody Map<String, Object> profileData,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = getUserIdFromUserDetails(userDetails);
        User user = userService.findById(userId);
        
        if (profileData.containsKey("realName")) {
            user.setRealName((String) profileData.get("realName"));
        }
        if (profileData.containsKey("email")) {
            user.setEmail((String) profileData.get("email"));
        }
        if (profileData.containsKey("phone")) {
            user.setPhone((String) profileData.get("phone"));
        }
        
        User updatedUser = userService.update(userId, user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "资料更新成功");
        response.put("data", updatedUser);
        return ResponseEntity.ok(response);
    }
    
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        if (userDetails instanceof com.example.backend.security.UserDetailsImpl) {
            return ((com.example.backend.security.UserDetailsImpl) userDetails).getId();
        }
        User user = userService.findByUsername(userDetails.getUsername());
        return user.getId();
    }
}
