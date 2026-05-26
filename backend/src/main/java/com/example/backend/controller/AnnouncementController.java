package com.example.backend.controller;

import com.example.backend.dto.AnnouncementDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Announcement;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<AnnouncementDTO>>> getTopAnnouncements() {
        List<AnnouncementDTO> announcements = announcementService.getTopAnnouncements();
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<AnnouncementDTO>>> getAnnouncements(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AnnouncementDTO> announcements = announcementService.getAnnouncements(page, size);
        return ResponseEntity.ok(ApiResponse.success(announcements));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> getDetail(@PathVariable Long id) {
        AnnouncementDTO announcement = announcementService.getDetail(id);
        return ResponseEntity.ok(ApiResponse.success(announcement));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> create(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(defaultValue = "NORMAL") Announcement.AnnouncementType type,
            @RequestParam(defaultValue = "false") Boolean isTop) {
        AnnouncementDTO announcement = announcementService.create(
                userDetails.getId(), title, content, type, isTop);
        return ResponseEntity.ok(ApiResponse.success("公告发布成功", announcement));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnnouncementDTO>> update(
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Announcement.AnnouncementType type,
            @RequestParam(required = false) Boolean isTop) {
        AnnouncementDTO announcement = announcementService.update(id, title, content, type, isTop);
        return ResponseEntity.ok(ApiResponse.success("公告更新成功", announcement));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("公告删除成功", null));
    }
}
