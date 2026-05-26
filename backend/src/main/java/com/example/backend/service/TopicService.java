package com.example.backend.service;

import com.example.backend.dto.TopicCommentDTO;
import com.example.backend.dto.TopicDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Topic;
import com.example.backend.entity.TopicComment;
import com.example.backend.entity.User;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.TopicCommentRepository;
import com.example.backend.repository.TopicRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final TopicCommentRepository commentRepository;
    private final UserRepository userRepository;

    public PageResponse<TopicDTO> getTopics(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Topic> topicPage;
        if (category != null && !category.isEmpty()) {
            topicPage = topicRepository.findByCategoryAndStatus(category, Topic.TopicStatus.NORMAL, pageable);
        } else {
            topicPage = topicRepository.findByStatusWithAuthor(Topic.TopicStatus.NORMAL, pageable);
        }
        Page<TopicDTO> dtoPage = topicPage.map(TopicDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public PageResponse<TopicDTO> getHotTopics(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Topic> topicPage = topicRepository.findHotTopics(Topic.TopicStatus.NORMAL, pageable);
        Page<TopicDTO> dtoPage = topicPage.map(TopicDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public TopicDTO getTopicById(Long id, Long currentUserId) {
        Topic topic = topicRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        if (topic.getStatus() == Topic.TopicStatus.DELETED) {
            throw new BusinessException("该话题已被删除");
        }

        topic.setViewCount(topic.getViewCount() + 1);
        topicRepository.save(topic);

        TopicDTO dto = TopicDTO.fromEntity(topic);
        return dto;
    }

    public PageResponse<TopicDTO> getMyTopics(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Topic> topicPage = topicRepository.findByAuthorIdWithAuthor(userId, pageable);
        Page<TopicDTO> dtoPage = topicPage.map(TopicDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public TopicDTO createTopic(Long userId, String title, String content, String category, String coverImage) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Topic topic = new Topic();
        topic.setAuthor(author);
        topic.setTitle(title);
        topic.setContent(content);
        topic.setCategory(category);
        topic.setCoverImage(coverImage);
        topic.setStatus(Topic.TopicStatus.NORMAL);

        Topic saved = topicRepository.save(topic);
        return TopicDTO.fromEntity(saved);
    }

    @Transactional
    public TopicDTO updateTopic(Long id, Long userId, String title, String content, String category, String coverImage) {
        Topic topic = topicRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        if (!topic.getAuthor().getId().equals(userId)) {
            throw new BusinessException("无权修改此话题");
        }

        if (title != null) topic.setTitle(title);
        if (content != null) topic.setContent(content);
        if (category != null) topic.setCategory(category);
        if (coverImage != null) topic.setCoverImage(coverImage);

        Topic saved = topicRepository.save(topic);
        return TopicDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteTopic(Long id, Long userId) {
        Topic topic = topicRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        if (!topic.getAuthor().getId().equals(userId)) {
            throw new BusinessException("无权删除此话题");
        }

        topic.setStatus(Topic.TopicStatus.DELETED);
        topicRepository.save(topic);
    }

    @Transactional
    public void likeTopic(Long id, Long userId) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
        topic.setLikeCount(topic.getLikeCount() + 1);
        topicRepository.save(topic);
    }

    @Transactional
    public void unlikeTopic(Long id, Long userId) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
        if (topic.getLikeCount() > 0) {
            topic.setLikeCount(topic.getLikeCount() - 1);
            topicRepository.save(topic);
        }
    }

    public PageResponse<TopicCommentDTO> getComments(Long topicId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<TopicComment> commentPage = commentRepository.findByTopicIdAndStatusWithUser(topicId, TopicComment.CommentStatus.NORMAL, pageable);

        Page<TopicCommentDTO> dtoPage = commentPage.map(comment -> {
            TopicCommentDTO dto = TopicCommentDTO.fromEntity(comment);
            List<TopicComment> replies = commentRepository.findRepliesByParentId(comment.getId(), TopicComment.CommentStatus.NORMAL);
            dto.setReplies(replies.stream().map(TopicCommentDTO::fromEntity).collect(Collectors.toList()));
            return dto;
        });

        return PageResponse.of(dtoPage);
    }

    @Transactional
    public TopicCommentDTO createComment(Long topicId, Long userId, String content, Long parentId) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicId));

        if (topic.getStatus() != Topic.TopicStatus.NORMAL) {
            throw new BusinessException("该话题已关闭评论");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        TopicComment comment = new TopicComment();
        comment.setTopic(topic);
        comment.setUser(user);
        comment.setContent(content);
        comment.setStatus(TopicComment.CommentStatus.NORMAL);

        if (parentId != null) {
            TopicComment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("TopicComment", "id", parentId));
            comment.setParent(parent);
        }

        TopicComment saved = commentRepository.save(comment);
        topic.setCommentCount(topic.getCommentCount() + 1);
        topicRepository.save(topic);

        return TopicCommentDTO.fromEntity(saved);
    }

    @Transactional
    public void deleteComment(Long id, Long userId) {
        TopicComment comment = commentRepository.findByIdWithUser(id)
                .orElseThrow(() -> new ResourceNotFoundException("TopicComment", "id", id));

        if (!comment.getUser().getId().equals(userId)) {
            throw new BusinessException("无权删除此评论");
        }

        comment.setStatus(TopicComment.CommentStatus.DELETED);
        commentRepository.save(comment);
    }

    @Transactional
    public void likeComment(Long id) {
        TopicComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TopicComment", "id", id));
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }
}
