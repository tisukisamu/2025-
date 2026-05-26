package com.example.backend.service;

import com.example.backend.entity.ServicePackage;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.ServicePackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicePackageService {

    private final ServicePackageRepository servicePackageRepository;

    public List<ServicePackage> getActivePackages() {
        return servicePackageRepository.findByStatusAndDeletedOrderBySortOrderAsc(1, 0);
    }

    public List<ServicePackage> getPackagesByType(String type) {
        return servicePackageRepository.findByTypeAndStatusAndDeletedOrderBySortOrderAsc(type, 1, 0);
    }

    public ServicePackage getPackageById(Long id) {
        return servicePackageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("服务套餐", "id", id));
    }

    @Transactional
    public ServicePackage createPackage(ServicePackage servicePackage) {
        return servicePackageRepository.save(servicePackage);
    }

    @Transactional
    public ServicePackage updatePackage(Long id, ServicePackage servicePackage) {
        ServicePackage existingPackage = getPackageById(id);
        
        existingPackage.setName(servicePackage.getName());
        existingPackage.setType(servicePackage.getType());
        existingPackage.setDescription(servicePackage.getDescription());
        existingPackage.setPrice(servicePackage.getPrice());
        existingPackage.setOriginalPrice(servicePackage.getOriginalPrice());
        existingPackage.setIncludes(servicePackage.getIncludes());
        existingPackage.setImage(servicePackage.getImage());
        existingPackage.setSortOrder(servicePackage.getSortOrder());
        existingPackage.setStatus(servicePackage.getStatus());
        
        return servicePackageRepository.save(existingPackage);
    }

    @Transactional
    public void deletePackage(Long id) {
        ServicePackage servicePackage = getPackageById(id);
        servicePackage.setDeleted(1);
        servicePackageRepository.save(servicePackage);
    }

    @Transactional
    public void updateStatus(Long id, Integer status) {
        ServicePackage servicePackage = getPackageById(id);
        servicePackage.setStatus(status);
        servicePackageRepository.save(servicePackage);
    }

    public boolean existsActivePackage(Long id) {
        return servicePackageRepository.existsByIdAndStatus(id, 1);
    }
}
