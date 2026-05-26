package com.club.fund.controller;

import com.club.fund.common.PageResult;
import com.club.fund.common.Result;
import com.club.fund.common.Constants;
import com.club.fund.dto.request.ClubCreateRequest;
import com.club.fund.dto.response.ClubResponse;
import com.club.fund.dto.response.UserResponse;
import com.club.fund.service.ClubService;
import com.club.fund.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/club")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping
    public Result<ClubResponse> createClub(@Valid @RequestBody ClubCreateRequest request) {
        return Result.success(clubService.createClub(request));
    }

    @PutMapping("/{id}")
    public Result<ClubResponse> updateClub(@PathVariable Long id, @Valid @RequestBody ClubCreateRequest request) {
        return Result.success(clubService.updateClub(id, request));
    }

    @GetMapping("/{id}")
    public Result<ClubResponse> getClubById(@PathVariable Long id) {
        return Result.success(clubService.getClubById(id));
    }

    @GetMapping("/list")
    public Result<Page<ClubResponse>> getClubList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentUserRole();
        return Result.success(clubService.getClubList(userId, role, pageable));
    }

    @GetMapping("/search")
    public Result<Page<ClubResponse>> searchClubs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(clubService.searchClubs(keyword, pageable));
    }

    @GetMapping("/my")
    public Result<List<ClubResponse>> getMyClubs() {
        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentUserRole();
        
        if ("president".equals(role)) {
            return Result.success(clubService.getClubsByPresidentId(userId));
        } else if ("teacher".equals(role)) {
            return Result.success(clubService.getClubsByTeacherId(userId));
        } else if (Constants.ROLE_ADMIN.equals(role)) {
            return Result.success(clubService.getAllActiveClubs());
        } else if ("member".equals(role)) {
            return Result.success(clubService.getClubsByMemberId(userId));
        }
        return Result.success(List.of());
    }

    @PostMapping("/{clubId}/member")
    public Result<Void> addMember(
            @PathVariable Long clubId,
            @RequestParam Long userId,
            @RequestParam(required = false) String position) {
        clubService.addMember(clubId, userId, position);
        return Result.success();
    }

    @DeleteMapping("/{clubId}/member/{userId}")
    public Result<Void> removeMember(@PathVariable Long clubId, @PathVariable Long userId) {
        clubService.removeMember(clubId, userId);
        return Result.success();
    }

    @PutMapping("/{clubId}/member/{userId}/position")
    public Result<Void> updateMemberPosition(
            @PathVariable Long clubId,
            @PathVariable Long userId,
            @RequestParam String position) {
        clubService.updateMemberPosition(clubId, userId, position);
        return Result.success();
    }

    @GetMapping("/{clubId}/members")
    public Result<Page<UserResponse>> getClubMembers(
            @PathVariable Long clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(clubService.getClubMembers(clubId, pageable));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<ClubResponse> updateClubStatus(@PathVariable Long id, @RequestParam Integer status) {
        return Result.success(clubService.updateClubStatus(id, status));
    }
}
