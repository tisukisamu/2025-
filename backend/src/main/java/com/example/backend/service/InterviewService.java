package com.example.backend.service;

import com.example.backend.dto.InterviewDTO;
import com.example.backend.entity.Interview;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;

    public List<Interview> getInterviewsByApplicationId(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId);
    }

    public Interview getInterviewById(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("面试记录不存在"));
    }

    @Transactional
    public Interview createInterview(InterviewDTO interviewDTO) {
        Interview interview = new Interview();
        interview.setApplicationId(interviewDTO.getApplicationId());
        interview.setInterviewTime(interviewDTO.getInterviewTime());
        interview.setLocation(interviewDTO.getLocation());
        interview.setInterviewer(interviewDTO.getInterviewer());
        interview.setResult(Interview.Result.PENDING);
        return interviewRepository.save(interview);
    }

    @Transactional
    public Interview updateInterview(Long id, InterviewDTO interviewDTO) {
        Interview interview = getInterviewById(id);
        interview.setInterviewTime(interviewDTO.getInterviewTime());
        interview.setLocation(interviewDTO.getLocation());
        interview.setInterviewer(interviewDTO.getInterviewer());
        return interviewRepository.save(interview);
    }

    @Transactional
    public void updateInterviewResult(Long id, Interview.Result result, String feedback) {
        Interview interview = getInterviewById(id);
        interview.setResult(result);
        interview.setFeedback(feedback);
        interviewRepository.save(interview);
    }
}
