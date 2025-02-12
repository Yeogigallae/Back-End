package com.umc.yeogi_gal_lae.global.error;

public enum ErrorStatus {
    _PARSING_ERROR("파싱 중 오류가 발생했습니다.");

    private final String message;

    ErrorStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
