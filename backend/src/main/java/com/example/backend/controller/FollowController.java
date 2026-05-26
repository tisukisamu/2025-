package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.dto.response.UserDTO;
import com.example.backend.entity.User;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FollowController {

    private final FollowService followService;

    @GetMapping("/following")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getFollowing(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserDTO> following = followService.getFollowing(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(following));
    }

    @GetMapping("/followers")
    public ResponseEntity<ApiResponse<PageResponse<UserDTO>>> getFollowers(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<UserDTO> followers = followService.getFollowers(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(followers));
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getFollowStats(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("followingCount", followService.getFollowingCount(userId));
        stats.put("followerCount", followService.getFollowerCount(userId));
        
        if (userDetails != null) {
            stats.put("isFollowing", followService.isFollowing(userDetails.getId(), userId) ? 1L : 0L);
        } else {
            stats.put("isFollowing", 0L);
        }
        
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> follow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.follow(userDetails.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("关注成功", null));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> unfollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        followService.unfollow(userDetails.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("取消关注成功", null));
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkFollow(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        boolean isFollowing = followService.isFollowing(userDetails.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success(isFollowing));
    }
}
