package com.example.backend.controller;

import com.example.backend.entity.TalentGroup;
import com.example.backend.entity.TalentPool;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.CompanyService;
import com.example.backend.service.TalentPoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TalentPoolController {

    private final TalentPoolService talentPoolService;
    private final CompanyService companyService;

    @GetMapping("/talent-pool")
    public ResponseEntity<Map<String, Object>> getTalentPool(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        List<TalentPool> talents = talentPoolService.getTalentPoolByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", talents);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/talent-pool")
    public ResponseEntity<Map<String, Object>> addToPool(
            @RequestParam Long resumeId,
            @RequestParam Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        TalentPool talent = talentPoolService.addToPool(companyId, resumeId, userId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "添加成功");
        response.put("data", talent);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/talent-pool/{id}/tags")
    public ResponseEntity<Map<String, Object>> updateTags(
            @PathVariable Long id,
            @RequestParam String tags) {
        TalentPool talent = talentPoolService.updateTalentTags(id, tags);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", talent);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/talent-pool/{id}/group")
    public ResponseEntity<Map<String, Object>> moveToGroup(
            @PathVariable Long id,
            @RequestParam Long groupId) {
        TalentPool talent = talentPoolService.moveToGroup(id, groupId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "移动成功");
        response.put("data", talent);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/talent-pool/{id}")
    public ResponseEntity<Map<String, Object>> removeFromPool(@PathVariable Long id) {
        talentPoolService.removeFromPool(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "移除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/talent-groups")
    public ResponseEntity<Map<String, Object>> getGroups(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        List<TalentGroup> groups = talentPoolService.getGroupsByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", groups);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/talent-groups")
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String color,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long companyId = companyService.getCompanyByUserId(userDetails.getId()).getId();
        TalentGroup group = talentPoolService.createGroup(companyId, name, description, color);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "创建成功");
        response.put("data", group);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/talent-groups/{id}")
    public ResponseEntity<Map<String, Object>> updateGroup(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String color) {
        TalentGroup group = talentPoolService.updateGroup(id, name, description, color);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", group);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/talent-groups/{id}")
    public ResponseEntity<Map<String, Object>> deleteGroup(@PathVariable Long id) {
        talentPoolService.deleteGroup(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
