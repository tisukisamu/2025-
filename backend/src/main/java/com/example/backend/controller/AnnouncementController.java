package com.example.backend.controller;

import com.example.backend.dto.AnnouncementDTO;
import com.example.backend.entity.Announcement;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", announcements);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAnnouncementById(@PathVariable Long id) {
        Announcement announcement = announcementService.getAnnouncementById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", announcement);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllAnnouncements() {
        List<Announcement> announcements = announcementService.getAllAnnouncements();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", announcements);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createAnnouncement(
            @Valid @RequestBody AnnouncementDTO announcementDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Announcement announcement = announcementService.createAnnouncement(announcementDTO, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "公告创建成功");
        response.put("data", announcement);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAnnouncement(
            @PathVariable Long id,
            @Valid @RequestBody AnnouncementDTO announcementDTO) {
        Announcement announcement = announcementService.updateAnnouncement(id, announcementDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "更新成功");
        response.put("data", announcement);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> publishAnnouncement(@PathVariable Long id) {
        announcementService.publishAnnouncement(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "公告已发布");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
