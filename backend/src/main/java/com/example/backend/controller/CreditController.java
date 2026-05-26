package com.example.backend.controller;

import com.example.backend.dto.CreditRecordDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/credit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CreditController {

    private final CreditService creditService;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCreditInfo(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Map<String, Object> info = creditService.getUserCreditInfo(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(info));
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<PageResponse<CreditRecordDTO>>> getCreditHistory(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CreditRecordDTO> history = creditService.getCreditHistory(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    @PostMapping("/daily-login")
    public ResponseEntity<ApiResponse<Void>> dailyLogin(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        creditService.addCreditForDailyLogin(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("签到成功", null));
    }
}
