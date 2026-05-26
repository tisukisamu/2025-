package com.example.backend.repository;

import com.example.backend.entity.AlumniTeamMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniTeamMessageRepository extends JpaRepository<AlumniTeamMessage, Long> {
    List<AlumniTeamMessage> findByTeamIdOrderByCreatedAtDesc(Long teamId);
}
