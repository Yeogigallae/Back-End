package com.umc.yeogi_gal_lae.api.notification.repository;

import com.umc.yeogi_gal_lae.api.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


    // 최신순으로 정렬하여 모든 알림 조회
    List<Notification> findAllByOrderByIdDesc();

}