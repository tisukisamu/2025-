package com.example.backend.controller;

import com.example.backend.dto.CompanyRatingDTO;
import com.example.backend.dto.InterviewEvaluationDTO;
import com.example.backend.entity.CandidateFeedback;
import com.example.backend.entity.CompanyRating;
import com.example.backend.entity.InterviewEvaluation;
import com.example.backend.security.UserDetailsImpl;
import com.example.backend.service.EvaluationService;
import jakarta.validation.Valid;
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
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/interview-evaluations")
    public ResponseEntity<Map<String, Object>> submitEvaluation(
            @Valid @RequestBody InterviewEvaluationDTO evaluationDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        InterviewEvaluation evaluation = evaluationService.submitEvaluation(evaluationDTO, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "评价提交成功");
        response.put("data", evaluation);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/interview-evaluations/{interviewId}")
    public ResponseEntity<Map<String, Object>> getEvaluation(@PathVariable Long interviewId) {
        InterviewEvaluation evaluation = evaluationService.getEvaluationByInterviewId(interviewId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", evaluation);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/candidate-feedback")
    public ResponseEntity<Map<String, Object>> submitFeedback(
            @RequestParam Long interviewId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String feedback,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CandidateFeedback candidateFeedback = evaluationService.submitFeedback(interviewId, userDetails.getId(), rating, feedback);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "反馈提交成功");
        response.put("data", candidateFeedback);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/candidate-feedback/{interviewId}")
    public ResponseEntity<Map<String, Object>> getFeedback(@PathVariable Long interviewId) {
        CandidateFeedback feedback = evaluationService.getFeedbackByInterviewId(interviewId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", feedback);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/company-ratings")
    public ResponseEntity<Map<String, Object>> submitRating(
            @Valid @RequestBody CompanyRatingDTO ratingDTO,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CompanyRating rating = evaluationService.submitRating(ratingDTO, userDetails.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "评价提交成功");
        response.put("data", rating);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company-ratings/{companyId}")
    public ResponseEntity<Map<String, Object>> getRatings(@PathVariable Long companyId) {
        List<CompanyRating> ratings = evaluationService.getRatingsByCompanyId(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", ratings);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company-ratings/statistics/{companyId}")
    public ResponseEntity<Map<String, Object>> getRatingStatistics(@PathVariable Long companyId) {
        Map<String, Object> stats = evaluationService.getRatingStatistics(companyId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", stats);
        return ResponseEntity.ok(response);
    }
}
