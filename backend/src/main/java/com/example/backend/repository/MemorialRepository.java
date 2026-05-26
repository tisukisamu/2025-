package com.example.backend.repository;

import com.example.backend.entity.MemorialAlbum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemorialRepository extends JpaRepository<MemorialAlbum, Long> {
    
    List<MemorialAlbum> findByPetIdAndDeletedOrderByCreatedAtDesc(Long petId, Integer deleted);
    
    List<MemorialAlbum> findByUserIdAndDeletedOrderByCreatedAtDesc(Long userId, Integer deleted);
    
    Page<MemorialAlbum> findByIsPublicAndDeletedOrderByCreatedAtDesc(Integer isPublic, Integer deleted, Pageable pageable);
    
    Optional<MemorialAlbum> findByIdAndDeleted(Long id, Integer deleted);
    
    boolean existsByIdAndUserId(Long id, Long userId);
}
