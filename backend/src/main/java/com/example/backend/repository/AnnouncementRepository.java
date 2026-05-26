package com.example.backend.repository;

import com.example.backend.entity.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    @Query("SELECT a FROM Announcement a JOIN FETCH a.author ORDER BY a.isTop DESC, a.createTime DESC")
    List<Announcement> findTop5WithAuthor();

    @Query(value = "SELECT a FROM Announcement a JOIN FETCH a.author ORDER BY a.isTop DESC, a.createTime DESC",
            countQuery = "SELECT COUNT(a) FROM Announcement a")
    Page<Announcement> findAllWithAuthor(Pageable pageable);

    @Query("SELECT a FROM Announcement a JOIN FETCH a.author WHERE a.id = :id")
    Optional<Announcement> findByIdWithAuthor(Long id);

    Page<Announcement> findByTypeOrderByIsTopDescCreateTimeDesc(Announcement.AnnouncementType type, Pageable pageable);

    Page<Announcement> findAllByOrderByIsTopDescCreateTimeDesc(Pageable pageable);

    List<Announcement> findTop5ByOrderByIsTopDescCreateTimeDesc();

    List<Announcement> findByIsTopTrueOrderByCreateTimeDesc();
}
