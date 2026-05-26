package com.example.backend.repository;

import com.example.backend.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findByIsPublishedTrue(Pageable pageable);
    
    List<Notification> findByIsPublishedTrue();
    
    Page<Notification> findByType(Notification.Type type, Pageable pageable);
    
    @Query("SELECT n FROM Notification n WHERE n.isPublished = true AND (n.targetRole = 'ALL' OR n.targetRole = :role)")
    List<Notification> findPublishedByTargetRole(@Param("role") Notification.TargetRole role);
    
    @Query("SELECT n FROM Notification n WHERE n.isPublished = true AND n.type = :type AND (n.targetRole = 'ALL' OR n.targetRole = :role)")
    List<Notification> findPublishedByTypeAndTargetRole(@Param("type") Notification.Type type, 
                                                         @Param("role") Notification.TargetRole role);
}
