package com.example.backend.service;

import com.example.backend.dto.AppointmentRequest;
import com.example.backend.dto.AppointmentResponse;
import com.example.backend.dto.PageResponse;
import com.example.backend.entity.Appointment;
import com.example.backend.entity.Pet;
import com.example.backend.entity.ServicePackage;
import com.example.backend.exception.BusinessException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.AppointmentRepository;
import com.example.backend.repository.PetRepository;
import com.example.backend.repository.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ServicePackageRepository servicePackageRepository;
    private final ProcessService processService;

    @Transactional
    public AppointmentResponse createAppointment(Long userId, AppointmentRequest request) {
        Pet pet = petRepository.findById(request.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("宠物", "id", request.getPetId()));
        
        if (!pet.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此宠物");
        }
        
        ServicePackage servicePackage = servicePackageRepository.findById(request.getPackageId())
                .orElseThrow(() -> new ResourceNotFoundException("服务套餐", "id", request.getPackageId()));
        
        Appointment appointment = new Appointment();
        appointment.setOrderNo(generateOrderNo());
        appointment.setUserId(userId);
        appointment.setPetId(request.getPetId());
        appointment.setPackageId(request.getPackageId());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setContactName(request.getContactName());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setAddress(request.getAddress());
        appointment.setRemark(request.getRemark());
        appointment.setStatus("pending");
        
        Appointment savedAppointment = appointmentRepository.save(appointment);
        
        processService.initProcess(savedAppointment.getId());
        
        return convertToResponse(savedAppointment);
    }

    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约", "id", id));
        return convertToResponse(appointment);
    }

    public PageResponse<AppointmentResponse> getUserAppointments(Long userId, String status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
        
        Page<Appointment> page;
        if (status != null && !status.isEmpty()) {
            page = appointmentRepository.findByUserIdAndStatusAndDeleted(userId, status, 0, pageable);
        } else {
            page = appointmentRepository.findByUserIdAndDeletedOrderByCreatedAtDesc(userId, 0, pageable);
        }
        
        return convertToPageResponse(page, pageNum, pageSize);
    }

    public PageResponse<AppointmentResponse> getAllAppointments(String status, int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by("createdAt").descending());
        
        Page<Appointment> page;
        if (status != null && !status.isEmpty()) {
            page = appointmentRepository.findByStatusAndDeleted(status, 0, pageable);
        } else {
            page = appointmentRepository.findByDeletedOrderByCreatedAtDesc(0, pageable);
        }
        
        return convertToPageResponse(page, pageNum, pageSize);
    }

    @Transactional
    public void updateStatus(Long id, String status, Long operatorId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约", "id", id));
        
        appointment.setStatus(status);
        
        if ("confirmed".equals(status)) {
            appointment.setOperatorId(operatorId);
        }
        
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id, Long userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("预约", "id", id));
        
        if (!appointment.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此预约");
        }
        
        if (!"pending".equals(appointment.getStatus())) {
            throw new BusinessException("当前状态不允许取消");
        }
        
        appointment.setStatus("cancelled");
        appointmentRepository.save(appointment);
    }

    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "PM" + timestamp + uuid;
    }

    private AppointmentResponse convertToResponse(Appointment appointment) {
        AppointmentResponse response = new AppointmentResponse();
        response.setId(appointment.getId());
        response.setOrderNo(appointment.getOrderNo());
        response.setUserId(appointment.getUserId());
        response.setPetId(appointment.getPetId());
        response.setPackageId(appointment.getPackageId());
        response.setAppointmentTime(appointment.getAppointmentTime());
        response.setContactName(appointment.getContactName());
        response.setContactPhone(appointment.getContactPhone());
        response.setAddress(appointment.getAddress());
        response.setRemark(appointment.getRemark());
        response.setStatus(appointment.getStatus());
        response.setOperatorId(appointment.getOperatorId());
        response.setCreatedAt(appointment.getCreatedAt());
        response.setUpdatedAt(appointment.getUpdatedAt());
        
        petRepository.findById(appointment.getPetId()).ifPresent(pet -> {
            response.setPetName(pet.getName());
            response.setPetType(pet.getType());
            response.setPetPhoto(pet.getPhoto());
        });
        
        servicePackageRepository.findById(appointment.getPackageId()).ifPresent(pkg -> {
            response.setPackageName(pkg.getName());
            response.setPackagePrice(pkg.getPrice());
        });
        
        return response;
    }

    private PageResponse<AppointmentResponse> convertToPageResponse(Page<Appointment> page, int pageNum, int pageSize) {
        return PageResponse.of(
                page.map(this::convertToResponse).getContent(),
                page.getTotalElements(),
                pageNum,
                pageSize
        );
    }
}
