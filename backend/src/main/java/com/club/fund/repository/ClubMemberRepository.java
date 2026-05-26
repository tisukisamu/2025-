package com.club.fund.repository;

import com.club.fund.entity.ClubMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubMemberRepository extends JpaRepository<ClubMember, Long> {

    List<ClubMember> findByClubId(Long clubId);

    Page<ClubMember> findByClubId(Long clubId, Pageable pageable);

    Optional<ClubMember> findByClubIdAndUserId(Long clubId, Long userId);

    boolean existsByClubIdAndUserId(Long clubId, Long userId);

    @Query("SELECT cm FROM ClubMember cm WHERE cm.club.id = :clubId AND cm.status = 1")
    List<ClubMember> findActiveMembersByClubId(@Param("clubId") Long clubId);

    @Query("SELECT cm FROM ClubMember cm WHERE cm.user.id = :userId AND cm.status = 1")
    List<ClubMember> findActiveClubsByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ClubMember cm SET cm.status = 0 WHERE cm.club.id = :clubId AND cm.user.id = :userId")
    int removeMember(@Param("clubId") Long clubId, @Param("userId") Long userId);

    @Query("SELECT COUNT(cm) FROM ClubMember cm WHERE cm.club.id = :clubId AND cm.status = 1")
    int countByClubId(@Param("clubId") Long clubId);
}
