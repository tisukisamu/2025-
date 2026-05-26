package com.club.fund.controller;

import com.club.fund.common.Result;
import com.club.fund.dto.request.ActivityCreateRequest;
import com.club.fund.dto.response.ActivityResponse;
import com.club.fund.service.ActivityService;
import com.club.fund.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @PostMapping
    public Result<ActivityResponse> createActivity(@Valid @RequestBody ActivityCreateRequest request) {
        return Result.success(activityService.createActivity(request));
    }

    @PutMapping("/{id}")
    public Result<ActivityResponse> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityCreateRequest request) {
        return Result.success(activityService.updateActivity(id, request));
    }

    @PostMapping("/{id}/publish")
    public Result<ActivityResponse> publishActivity(@PathVariable Long id) {
        return Result.success(activityService.publishActivity(id));
    }

    @PostMapping("/{id}/submit")
    public Result<ActivityResponse> submitForReview(@PathVariable Long id) {
        return Result.success(activityService.submitForReview(id));
    }

    @PostMapping("/{id}/approve")
    public Result<ActivityResponse> approveActivity(@PathVariable Long id) {
        return Result.success(activityService.approveActivity(id));
    }

    @PostMapping("/{id}/reject")
    public Result<ActivityResponse> rejectActivity(@PathVariable Long id) {
        return Result.success(activityService.rejectActivity(id));
    }

    @GetMapping("/{id}")
    public Result<ActivityResponse> getActivityById(@PathVariable Long id) {
        return Result.success(activityService.getActivityById(id));
    }

    @GetMapping("/list")
    public Result<Page<ActivityResponse>> getActivityList(
            @RequestParam(required = false) Long clubId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(activityService.getActivityList(clubId, pageable));
    }

    @GetMapping("/search")
    public Result<Page<ActivityResponse>> searchActivities(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(activityService.searchActivities(keyword, pageable));
    }

    @GetMapping("/review-list")
    public Result<Page<ActivityResponse>> getReviewList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentUserRole();
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(activityService.getActivityReviewList(userId, role, pageable));
    }

    @GetMapping("/my-created")
    public Result<Page<ActivityResponse>> getMyCreatedActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        String role = SecurityUtil.getCurrentUserRole();
        Pageable pageable = PageRequest.of(page, size);
        return Result.success(activityService.getMyCreatedActivities(userId, role, pageable));
    }

    @GetMapping("/my-signups")
    public Result<java.util.List<ActivityResponse>> getMySignups() {
        Long userId = SecurityUtil.getCurrentUserId();
        return Result.success(activityService.getMySignupActivities(userId));
    }

    @PostMapping("/{id}/signup")
    public Result<Void> signupActivity(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        activityService.signupActivity(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}/signup")
    public Result<Void> cancelSignup(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        activityService.cancelSignup(id, userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return Result.success();
    }
}
