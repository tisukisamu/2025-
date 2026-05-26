package com.club.fund.repository;

import com.club.fund.entity.Club;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByClubCode(String clubCode);

    boolean existsByClubCode(String clubCode);

    @Query("SELECT c FROM Club c WHERE c.deleted = 0")
    Page<Club> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Club c WHERE c.deleted = 0")
    List<Club> findAllActiveList();

    @Query("SELECT c FROM Club c WHERE c.president.id = :presidentId AND c.deleted = 0")
    List<Club> findByPresidentId(@Param("presidentId") Long presidentId);

    @Query("SELECT c FROM Club c WHERE c.teacher.id = :teacherId AND c.deleted = 0")
    List<Club> findByTeacherId(@Param("teacherId") Long teacherId);

    @Query("SELECT c FROM Club c WHERE c.category = :category AND c.deleted = 0")
    List<Club> findByCategory(@Param("category") String category);

    @Query("SELECT c FROM Club c WHERE c.clubName LIKE %:keyword% AND c.deleted = 0")
    Page<Club> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
