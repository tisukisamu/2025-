package com.example.backend.repository;

import com.example.backend.entity.ServicePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePackageRepository extends JpaRepository<ServicePackage, Long> {
    
    List<ServicePackage> findByStatusAndDeletedOrderBySortOrderAsc(Integer status, Integer deleted);
    
    List<ServicePackage> findByTypeAndStatusAndDeletedOrderBySortOrderAsc(String type, Integer status, Integer deleted);
    
    boolean existsByIdAndStatus(Long id, Integer status);
}
