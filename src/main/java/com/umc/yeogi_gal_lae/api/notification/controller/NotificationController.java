package com.umc.yeogi_gal_lae.api.notification.controller;

import com.umc.yeogi_gal_lae.api.notification.dto.NotificationDto;
import com.umc.yeogi_gal_lae.api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 알림 리스트 조회 API (GET)
     */
    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotifications() {
        // Mock 데이터를 생성해 반환
        List<NotificationDto> mockData = List.of(
                new NotificationDto(1L, "투표 종료", "가보자고 방의 투표 결과를 확인해보세요!", "VOTE"),
                new NotificationDto(2L, "코스 짜기 완료", "가보자고 방의 생성된 코스를 확인해보세요!", "COURSE"),
                new NotificationDto(3L, "예산 정하기 완료", "가보자고 방의 예산을 확인해보세요!", "BUDGET")
        );

        return ResponseEntity.ok(mockData);
    }
}