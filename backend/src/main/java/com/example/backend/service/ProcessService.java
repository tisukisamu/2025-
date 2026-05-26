package com.example.backend.service;

import com.example.backend.dto.ProcessResponse;
import com.example.backend.dto.ProcessUpdateRequest;
import com.example.backend.entity.ProcessStage;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ProcessRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;
    private final ObjectMapper objectMapper;

    private static final List<String> STAGES = Arrays.asList(
            "confirmed", "pickup", "farewell", "cremation", "processing", "memorial", "completed"
    );

    @Transactional
    public void initProcess(Long appointmentId) {
        for (String stage : STAGES) {
            ProcessStage processStage = new ProcessStage();
            processStage.setAppointmentId(appointmentId);
            processStage.setStage(stage);
            processStage.setStatus("pending");
            processRepository.save(processStage);
        }
    }

    public List<ProcessResponse> getProcessByAppointmentId(Long appointmentId) {
        List<ProcessStage> stages = processRepository.findByAppointmentIdOrderByCreatedAtAsc(appointmentId);
        return stages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProcessResponse updateProcess(Long appointmentId, ProcessUpdateRequest request, Long operatorId) {
        ProcessStage processStage = processRepository.findByAppointmentIdAndStage(appointmentId, request.getStage())
                .orElseThrow(() -> new ResourceNotFoundException("流程阶段", "appointmentId和stage", appointmentId));
        
        processStage.setStatus(request.getStatus());
        processStage.setOperatorId(operatorId);
        processStage.setDescription(request.getDescription());
        
        if ("processing".equals(request.getStatus())) {
            processStage.setStartTime(LocalDateTime.now());
        } else if ("completed".equals(request.getStatus())) {
            processStage.setEndTime(LocalDateTime.now());
        }
        
        if (request.getPhotos() != null && !request.getPhotos().isEmpty()) {
            try {
                processStage.setPhotos(objectMapper.writeValueAsString(request.getPhotos()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("照片数据处理失败");
            }
        }
        
        if (request.getVideos() != null && !request.getVideos().isEmpty()) {
            try {
                processStage.setVideos(objectMapper.writeValueAsString(request.getVideos()));
            } catch (JsonProcessingException e) {
                throw new BusinessException("视频数据处理失败");
            }
        }
        
        ProcessStage savedStage = processRepository.save(processStage);
        return convertToResponse(savedStage);
    }

    private ProcessResponse convertToResponse(ProcessStage stage) {
        ProcessResponse response = new ProcessResponse();
        response.setId(stage.getId());
        response.setAppointmentId(stage.getAppointmentId());
        response.setStage(stage.getStage());
        response.setStatus(stage.getStatus());
        response.setOperatorId(stage.getOperatorId());
        response.setDescription(stage.getDescription());
        response.setStartTime(stage.getStartTime());
        response.setEndTime(stage.getEndTime());
        response.setCreatedAt(stage.getCreatedAt());
        response.setUpdatedAt(stage.getUpdatedAt());
        
        if (stage.getPhotos() != null && !stage.getPhotos().isEmpty()) {
            try {
                response.setPhotos(objectMapper.readValue(stage.getPhotos(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                response.setPhotos(List.of());
            }
        }
        
        if (stage.getVideos() != null && !stage.getVideos().isEmpty()) {
            try {
                response.setVideos(objectMapper.readValue(stage.getVideos(), new TypeReference<List<String>>() {}));
            } catch (JsonProcessingException e) {
                response.setVideos(List.of());
            }
        }
        
        return response;
    }
}
