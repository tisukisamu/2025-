package com.example.backend.controller;

import com.example.backend.dto.CommunityCommentDTO;
import com.example.backend.dto.CommunityPostDTO;
import com.example.backend.dto.CreateCommunityCommentRequest;
import com.example.backend.dto.CreateCommunityPostRequest;
import com.example.backend.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts() {
        List<CommunityPostDTO> list = communityService.getPosts();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<Map<String, Object>> createPost(@Valid @RequestBody CreateCommunityPostRequest request) {
        CommunityPostDTO post = communityService.createPost(request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "发布成功");
        response.put("data", post);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, Object>> deletePost(@PathVariable Long postId) {
        communityService.deletePost(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long postId) {
        List<CommunityCommentDTO> list = communityService.getComments(postId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommunityCommentRequest request) {
        CommunityCommentDTO comment = communityService.createComment(postId, request);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "评论成功");
        response.put("data", comment);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "删除成功");
        return ResponseEntity.ok(response);
    }
}
