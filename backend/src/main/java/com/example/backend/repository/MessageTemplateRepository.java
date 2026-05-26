package com.example.backend.repository;

import com.example.backend.entity.MessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {
    Optional<MessageTemplate> findByCode(String code);
    Optional<MessageTemplate> findByName(String name);
}
