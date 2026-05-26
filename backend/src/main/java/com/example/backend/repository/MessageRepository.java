package com.example.backend.repository;

import com.example.backend.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId);
    List<Message> findByReceiverIdAndIsReadFalseOrderByCreatedAtDesc(Long receiverId);
    Page<Message> findByReceiverIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);
    long countByReceiverIdAndIsReadFalse(Long receiverId);
}
