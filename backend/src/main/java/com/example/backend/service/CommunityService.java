package com.example.backend.service;

import com.example.backend.dto.CommunityCommentDTO;
import com.example.backend.dto.CommunityPostDTO;
import com.example.backend.dto.CreateCommunityCommentRequest;
import com.example.backend.dto.CreateCommunityPostRequest;
import com.example.backend.entity.CommunityComment;
import com.example.backend.entity.CommunityPost;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CommunityCommentRepository;
import com.example.backend.repository.CommunityPostRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityPostRepository communityPostRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final UserRepository userRepository;

    public List<CommunityPostDTO> getPosts() {
        List<CommunityPost> posts = communityPostRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .map(post -> {
                    List<CommunityCommentDTO> comments = communityCommentRepository.findByPostIdOrderByCreatedAtAsc(post.getId()).stream()
                            .map(CommunityCommentDTO::fromEntity)
                            .collect(Collectors.toList());
                    long count = communityCommentRepository.countByPostId(post.getId());
                    return CommunityPostDTO.fromEntity(post, count, comments);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public CommunityPostDTO createPost(CreateCommunityPostRequest request) {
        User user = getCurrentUser();
        CommunityPost post = new CommunityPost();
        post.setContent(request.getContent().trim());
        post.setImagePath(normalizeImagePath(request.getImagePath()));
        post.setUser(user);

        CommunityPost saved = communityPostRepository.save(post);
        return CommunityPostDTO.fromEntity(saved, 0L, List.of());
    }

    @Transactional
    public void deletePost(Long postId) {
        User user = getCurrentUser();
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("社区动态", "id", postId));
        if (!post.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new BusinessException(403, "无权删除该动态");
        }
        communityCommentRepository.deleteByPostId(postId);
        communityPostRepository.delete(post);
    }

    public List<CommunityCommentDTO> getComments(Long postId) {
        if (!communityPostRepository.existsById(postId)) {
            throw new ResourceNotFoundException("社区动态", "id", postId);
        }
        return communityCommentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(CommunityCommentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommunityCommentDTO createComment(Long postId, CreateCommunityCommentRequest request) {
        User user = getCurrentUser();
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("社区动态", "id", postId));

        CommunityComment comment = new CommunityComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent().trim());
        CommunityComment saved = communityCommentRepository.save(comment);
        return CommunityCommentDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User user = getCurrentUser();
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("评论", "id", commentId));
        if (!comment.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new BusinessException(403, "无权删除该评论");
        }
        communityCommentRepository.delete(comment);
    }

    private String normalizeImagePath(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        String path = raw.trim();
        if (!path.startsWith("/upload/")) {
            throw new BusinessException(400, "图片地址必须是相对上传路径");
        }
        return path;
    }

    private User getCurrentUser() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("用户", "id", userDetails.getId()));
    }
}
