package com.example.backend.repository;

import com.example.backend.entity.AlumniTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlumniTeamRepository extends JpaRepository<AlumniTeam, Long> {
    List<AlumniTeam> findByStatusOrderByCreatedAtDesc(AlumniTeam.Status status);
}
