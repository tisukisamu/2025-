package com.example.backend.service;

import com.example.backend.dto.FeedbackDTO;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Feedback;
import com.example.backend.entity.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.FeedbackRepository;
import com.example.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public PageResponse<FeedbackDTO> getMyFeedbacks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Feedback> feedbackPage = feedbackRepository.findByUserIdWithUser(userId, pageable);
        Page<FeedbackDTO> dtoPage = feedbackPage.map(FeedbackDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    public PageResponse<FeedbackDTO> getAllFeedbacks(Feedback.FeedbackStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Feedback> feedbackPage;
        if (status != null) {
            feedbackPage = feedbackRepository.findByStatusWithUser(status, pageable);
        } else {
            feedbackPage = feedbackRepository.findAllWithUser(pageable);
        }
        Page<FeedbackDTO> dtoPage = feedbackPage.map(FeedbackDTO::fromEntity);
        return PageResponse.of(dtoPage);
    }

    @Transactional
    public FeedbackDTO create(Long userId, Feedback.FeedbackType type, String title,
                          String content, String contactInfo, String images) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Feedback feedback = new Feedback();
        feedback.setUser(user);
        feedback.setType(type);
        feedback.setTitle(title);
        feedback.setContent(content);
        feedback.setContactInfo(contactInfo);
        feedback.setImages(images);
        feedback.setStatus(Feedback.FeedbackStatus.PENDING);

        Feedback saved = feedbackRepository.save(feedback);
        return FeedbackDTO.fromEntity(saved);
    }

    @Transactional
    public FeedbackDTO reply(Long id, Long replierId, String replyContent) {
        Feedback feedback = feedbackRepository.findByIdWithUsers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));

        User replier = userRepository.findById(replierId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", replierId));

        feedback.setReplyContent(replyContent);
        feedback.setReplier(replier);
        feedback.setReplyTime(LocalDateTime.now());
        feedback.setStatus(Feedback.FeedbackStatus.RESOLVED);

        Feedback saved = feedbackRepository.save(feedback);
        return FeedbackDTO.fromEntity(saved);
    }

    @Transactional
    public void updateStatus(Long id, Feedback.FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", id));
        feedback.setStatus(status);
        feedbackRepository.save(feedback);
    }
}
