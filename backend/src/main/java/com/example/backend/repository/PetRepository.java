package com.example.backend.repository;

import com.example.backend.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    
    List<Pet> findByUserIdAndDeletedOrderByCreatedAtDesc(Long userId, Integer deleted);
    
    Page<Pet> findByUserIdAndDeleted(Long userId, Integer deleted, Pageable pageable);
    
    List<Pet> findByTypeAndDeleted(String type, Integer deleted);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
