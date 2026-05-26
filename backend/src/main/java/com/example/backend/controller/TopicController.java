package com.example.backend.controller;

import com.example.backend.dto.TopicCommentDTO;
import com.example.backend.dto.TopicDTO;
import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<TopicDTO>>> getTopics(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicDTO> topics = topicService.getTopics(category, page, size);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<PageResponse<TopicDTO>>> getHotTopics(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicDTO> topics = topicService.getHotTopics(page, size);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDTO>> getTopicById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        TopicDTO topic = topicService.getTopicById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(topic));
    }

    @GetMapping("/mine")
    public ResponseEntity<ApiResponse<PageResponse<TopicDTO>>> getMyTopics(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicDTO> topics = topicService.getMyTopics(userDetails.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TopicDTO>> createTopic(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String coverImage) {
        TopicDTO topic = topicService.createTopic(
                userDetails.getId(), title, content, category, coverImage);
        return ResponseEntity.ok(ApiResponse.success("发布成功", topic));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TopicDTO>> updateTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String coverImage) {
        TopicDTO topic = topicService.updateTopic(
                id, userDetails.getId(), title, content, category, coverImage);
        return ResponseEntity.ok(ApiResponse.success("更新成功", topic));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        topicService.deleteTopic(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> likeTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        topicService.likeTopic(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeTopic(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        topicService.unlikeTopic(id, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("取消点赞", null));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<PageResponse<TopicCommentDTO>>> getComments(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<TopicCommentDTO> comments = topicService.getComments(id, page, size);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<TopicCommentDTO>> createComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String content,
            @RequestParam(required = false) Long parentId) {
        TopicCommentDTO comment = topicService.createComment(id, userDetails.getId(), content, parentId);
        return ResponseEntity.ok(ApiResponse.success("评论成功", comment));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        topicService.deleteComment(commentId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<ApiResponse<Void>> likeComment(@PathVariable Long commentId) {
        topicService.likeComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("点赞成功", null));
    }
}
