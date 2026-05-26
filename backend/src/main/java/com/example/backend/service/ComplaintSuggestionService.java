package com.example.backend.service;

import com.example.backend.dto.ComplaintSuggestionDTO;
import com.example.backend.entity.ComplaintSuggestion;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ComplaintSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintSuggestionService {

    private final ComplaintSuggestionRepository complaintSuggestionRepository;

    public List<ComplaintSuggestion> getComplaintsByUserId(Long userId) {
        return complaintSuggestionRepository.findByUserId(userId);
    }

    public List<ComplaintSuggestion> getAllComplaints() {
        return complaintSuggestionRepository.findAll();
    }

    public List<ComplaintSuggestion> getComplaintsByStatus(ComplaintSuggestion.Status status) {
        return complaintSuggestionRepository.findByStatus(status);
    }

    public ComplaintSuggestion getComplaintById(Long id) {
        return complaintSuggestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("投诉建议不存在"));
    }

    @Transactional
    public ComplaintSuggestion createComplaint(ComplaintSuggestionDTO dto, Long userId) {
        ComplaintSuggestion complaint = new ComplaintSuggestion();
        complaint.setUserId(userId);
        complaint.setType(ComplaintSuggestion.Type.valueOf(dto.getType()));
        complaint.setTitle(dto.getTitle());
        complaint.setContent(dto.getContent());
        complaint.setStatus(ComplaintSuggestion.Status.PENDING);
        return complaintSuggestionRepository.save(complaint);
    }

    @Transactional
    public ComplaintSuggestion handleComplaint(Long id, Long handlerId, String result, ComplaintSuggestion.Status status) {
        ComplaintSuggestion complaint = getComplaintById(id);
        complaint.setHandlerId(handlerId);
        complaint.setHandlingResult(result);
        complaint.setStatus(status);
        return complaintSuggestionRepository.save(complaint);
    }
}
