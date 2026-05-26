package com.club.fund.repository;

import com.club.fund.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiverId(Long receiverId, Pageable pageable);

    List<Notification> findByReceiverIdAndIsRead(Long receiverId, Integer isRead);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.id = :receiverId AND n.isRead = 0")
    int countUnreadByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT n FROM Notification n WHERE n.receiver.id = :receiverId AND n.type = :type ORDER BY n.createTime DESC")
    Page<Notification> findByReceiverIdAndType(@Param("receiverId") Long receiverId, @Param("type") String type, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.id = :id")
    int markAsRead(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = 1 WHERE n.receiver.id = :receiverId")
    int markAllAsRead(@Param("receiverId") Long receiverId);
}
