package com.example.backend.repository;

import com.example.backend.entity.AlumniTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumniTeamMemberRepository extends JpaRepository<AlumniTeamMember, Long> {
    List<AlumniTeamMember> findByUserIdOrderByJoinedAtDesc(Long userId);

    List<AlumniTeamMember> findByTeamIdOrderByJoinedAtAsc(Long teamId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    Optional<AlumniTeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    long countByTeamId(Long teamId);
}
