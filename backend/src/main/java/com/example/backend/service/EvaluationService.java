package com.example.backend.service;

import com.example.backend.dto.CompanyRatingDTO;
import com.example.backend.dto.InterviewEvaluationDTO;
import com.example.backend.entity.CandidateFeedback;
import com.example.backend.entity.CompanyRating;
import com.example.backend.entity.InterviewEvaluation;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.CandidateFeedbackRepository;
import com.example.backend.repository.CompanyRatingRepository;
import com.example.backend.repository.InterviewEvaluationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final InterviewEvaluationRepository evaluationRepository;
    private final CandidateFeedbackRepository feedbackRepository;
    private final CompanyRatingRepository ratingRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public InterviewEvaluation submitEvaluation(InterviewEvaluationDTO evaluationDTO, Long interviewerId) {
        InterviewEvaluation evaluation = new InterviewEvaluation();
        evaluation.setInterviewId(evaluationDTO.getInterviewId());
        evaluation.setInterviewerId(interviewerId);
        
        try {
            evaluation.setScores(objectMapper.writeValueAsString(evaluationDTO.getScores()));
        } catch (JsonProcessingException e) {
            throw new BusinessException("评分数据格式错误");
        }
        
        evaluation.setComments(evaluationDTO.getComments());
        evaluation.setRecommendation(InterviewEvaluation.Recommendation.valueOf(evaluationDTO.getRecommendation()));
        evaluation.setOverallScore(calculateOverallScore(evaluationDTO.getScores()));
        
        return evaluationRepository.save(evaluation);
    }

    public InterviewEvaluation getEvaluationByInterviewId(Long interviewId) {
        List<InterviewEvaluation> evaluations = evaluationRepository.findByInterviewId(interviewId);
        if (evaluations.isEmpty()) {
            return null;
        }
        return evaluations.get(0);
    }

    private Integer calculateOverallScore(Map<String, Integer> scores) {
        if (scores == null || scores.isEmpty()) {
            return 0;
        }
        return (int) scores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    @Transactional
    public CandidateFeedback submitFeedback(Long interviewId, Long userId, Integer rating, String feedback) {
        if (feedbackRepository.findByInterviewIdAndUserId(interviewId, userId).isPresent()) {
            throw new BusinessException("您已提交过反馈");
        }

        CandidateFeedback candidateFeedback = new CandidateFeedback();
        candidateFeedback.setInterviewId(interviewId);
        candidateFeedback.setUserId(userId);
        candidateFeedback.setRating(rating);
        candidateFeedback.setFeedback(feedback);
        return feedbackRepository.save(candidateFeedback);
    }

    public CandidateFeedback getFeedbackByInterviewId(Long interviewId) {
        return feedbackRepository.findByInterviewIdAndUserId(interviewId, null).orElse(null);
    }

    @Transactional
    public CompanyRating submitRating(CompanyRatingDTO ratingDTO, Long userId) {
        if (ratingRepository.existsByCompanyIdAndUserId(ratingDTO.getCompanyId(), userId)) {
            throw new BusinessException("您已评价过该企业");
        }

        CompanyRating rating = new CompanyRating();
        rating.setCompanyId(ratingDTO.getCompanyId());
        rating.setUserId(userId);
        rating.setRating(ratingDTO.getRating());
        rating.setComment(ratingDTO.getComment());
        return ratingRepository.save(rating);
    }

    public List<CompanyRating> getRatingsByCompanyId(Long companyId) {
        return ratingRepository.findByCompanyId(companyId);
    }

    public Map<String, Object> getRatingStatistics(Long companyId) {
        List<CompanyRating> ratings = ratingRepository.findByCompanyId(companyId);

        double averageRating = ratings.stream()
                .mapToInt(CompanyRating::getRating)
                .average()
                .orElse(0);

        Map<Integer, Long> distribution = ratings.stream()
                .collect(Collectors.groupingBy(CompanyRating::getRating, Collectors.counting()));

        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", Math.round(averageRating * 10) / 10.0);
        stats.put("totalRatings", ratings.size());
        stats.put("distribution", distribution);
        return stats;
    }
}
