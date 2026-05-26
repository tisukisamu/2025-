package com.example.backend.controller;

import com.example.backend.common.Result;
import com.example.backend.dto.MemorialRequest;
import com.example.backend.dto.MemorialResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.MemorialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memorials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MemorialController {

    private final MemorialService memorialService;

    @GetMapping("/public")
    public Result<PageResponse<MemorialResponse>> getPublicMemorials(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResponse<MemorialResponse> result = memorialService.getPublicMemorials(pageNum, pageSize);
        return Result.success(result);
    }

    @GetMapping("/pet/{petId}")
    public Result<List<MemorialResponse>> getPetMemorials(@PathVariable Long petId) {
        List<MemorialResponse> memorials = memorialService.getPetMemorials(petId);
        return Result.success(memorials);
    }

    @GetMapping("/{id}")
    public Result<MemorialResponse> getMemorialById(@PathVariable Long id) {
        MemorialResponse memorial = memorialService.getMemorialById(id);
        return Result.success(memorial);
    }

    @PostMapping
    public Result<MemorialResponse> createMemorial(@Valid @RequestBody MemorialRequest request) {
        Long userId = getCurrentUserId();
        MemorialResponse memorial = memorialService.createMemorial(userId, request);
        return Result.success("创建成功", memorial);
    }

    @PutMapping("/{id}")
    public Result<MemorialResponse> updateMemorial(@PathVariable Long id, @Valid @RequestBody MemorialRequest request) {
        Long userId = getCurrentUserId();
        MemorialResponse memorial = memorialService.updateMemorial(id, userId, request);
        return Result.success("更新成功", memorial);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteMemorial(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        memorialService.deleteMemorial(id, userId);
        return Result.success("删除成功", null);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return Long.parseLong(authentication.getName());
    }
}
