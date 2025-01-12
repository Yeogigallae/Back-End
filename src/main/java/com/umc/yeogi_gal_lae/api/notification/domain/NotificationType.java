package com.umc.yeogi_gal_lae.api.notification.domain;

public enum NotificationType {
    VOTE_ENDED("투표 종료", "투표 결과를 확인해보세요!"),
    VOTE_STARTED("투표 시작", "투표가 시작되었습니다!"),
    COURSE_COMPLETED("코스짜기 완료", "생성된 코스를 확인해보세요!"),
    COURSE_STARTED("코스짜기 시작", "코스짜기가 시작되었습니다!"),
    BUDGET_COMPLETED("예산정하기 완료", "예산을 확인해보세요!"),
    BUDGET_STARTED("예산정하기 시작", "예산 정하기가 시작되었습니다!");

    private final String title;
    private final String message;

    NotificationType(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}