package com.umc.yeogi_gal_lae.api.notification.repository;

import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 최신순 정렬
    List<Notification> findAllByOrderByCreatedAtDesc();

    Optional<Notification> findById(Long id);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false AND n.userEmail = :userEmail")
    long countUnreadNotificationsByUser(@Param("userEmail") String userEmail);
}