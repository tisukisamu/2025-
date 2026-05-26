package com.example.backend.repository;

import com.example.backend.entity.FileRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, Long> {
    
    List<FileRecord> findByEntityTypeAndEntityId(String entityType, Long entityId);
    
    Optional<FileRecord> findByFilePath(String filePath);
    
    void deleteByFilePath(String filePath);
    
    List<FileRecord> findByUploaderIdOrderByCreatedAtDesc(Long uploaderId);
}
