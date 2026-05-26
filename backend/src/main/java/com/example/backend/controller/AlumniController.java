package com.example.backend.controller;

import com.example.backend.dto.*;
import com.example.backend.service.AlumniService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alumni")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AlumniController {

    private final AlumniService alumniService;

    @GetMapping("/profile/me")
    public ResponseEntity<Map<String, Object>> getMyProfile() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", alumniService.getMyProfile());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/me")
    public ResponseEntity<Map<String, Object>> updateMyProfile(@RequestBody UpdateAlumniProfileRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", alumniService.updateMyProfile(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyProfiles(@RequestParam(defaultValue = "10") Double radiusKm) {
        List<AlumniProfileDTO> list = alumniService.getNearbyProfiles(radiusKm);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ranking")
    public ResponseEntity<Map<String, Object>> getNearbyRanking(
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        List<AlumniRankingDTO> list = alumniService.getNearbyRanking(radiusKm, limit);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams")
    public ResponseEntity<Map<String, Object>> createTeam(@Valid @RequestBody CreateAlumniTeamRequest request) {
        AlumniTeamDTO team = alumniService.createTeam(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", team);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams/nearby")
    public ResponseEntity<Map<String, Object>> getNearbyTeams(@RequestParam(defaultValue = "10") Double radiusKm) {
        List<AlumniTeamDTO> list = alumniService.getNearbyTeams(radiusKm);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams/my")
    public ResponseEntity<Map<String, Object>> getMyTeams() {
        List<AlumniTeamDTO> list = alumniService.getMyTeams();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams/{teamId}")
    public ResponseEntity<Map<String, Object>> getTeamDetail(@PathVariable Long teamId) {
        AlumniTeamDetailDTO detail = alumniService.getTeamDetail(teamId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", detail);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams/{teamId}/join")
    public ResponseEntity<Map<String, Object>> joinTeam(@PathVariable Long teamId) {
        AlumniTeamDTO team = alumniService.joinTeam(teamId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "加入成功");
        response.put("data", team);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams/{teamId}/quit")
    public ResponseEntity<Map<String, Object>> quitTeam(@PathVariable Long teamId) {
        alumniService.quitTeam(teamId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "已退出小队");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teams/{teamId}/messages")
    public ResponseEntity<Map<String, Object>> getTeamMessages(@PathVariable Long teamId) {
        List<AlumniTeamMessageDTO> list = alumniService.getTeamMessages(teamId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/teams/{teamId}/messages")
    public ResponseEntity<Map<String, Object>> createTeamMessage(
            @PathVariable Long teamId,
            @Valid @RequestBody CreateAlumniTeamMessageRequest request) {
        AlumniTeamMessageDTO msg = alumniService.createTeamMessage(teamId, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "发送成功");
        response.put("data", msg);
        return ResponseEntity.ok(response);
    }
}
