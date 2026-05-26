package com.example.backend.repository;

import com.example.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    List<Message> findByAlbumIdAndDeletedOrderByCreatedAtDesc(Long albumId, Integer deleted);
    
    Page<Message> findByAlbumIdAndDeleted(Long albumId, Integer deleted, Pageable pageable);
}
