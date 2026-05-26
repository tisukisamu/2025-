package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.entity.SearchHistory;
import com.example.backend.entity.User;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search-history")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getRecentKeywords(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "10") int limit) {
        List<String> keywords = searchHistoryService.getRecentKeywords(userDetails.getId(), limit);
        return ResponseEntity.ok(ApiResponse.success(keywords));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> saveSearchHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword) {
        User user = userRepository.findById(userDetails.getId()).orElse(null);
        if (user != null) {
            searchHistoryService.saveSearchHistory(userDetails.getId(), user, keyword);
        }
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteSearchHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword) {
        searchHistoryService.deleteSearchHistory(userDetails.getId(), keyword);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearSearchHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        searchHistoryService.clearSearchHistory(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("清空成功", null));
    }
}
