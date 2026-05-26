package com.example.backend.repository;

import com.example.backend.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByStatus(Announcement.Status status);
    List<Announcement> findByType(Announcement.Type type);
}
