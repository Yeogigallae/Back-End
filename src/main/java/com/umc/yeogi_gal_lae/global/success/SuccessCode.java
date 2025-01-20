package com.umc.yeogi_gal_lae.global.success;

import com.umc.yeogi_gal_lae.global.common.response.ReasonDto;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode implements BaseStatus {
    // Common Success
    OK(HttpStatus.OK, "COMMON_200", "성공적으로 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON_201", "성공적으로 생성되었습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT, "COMMON_204", "성공적으로 삭제되었습니다."),

    // User Success
    USER_FETCH_OK(HttpStatus.OK, "USER_200", "유저 정보 조회 성공"),
    TOKEN_REISSUE_OK(HttpStatus.OK, "TOKEN_200", "토큰 재발급 성공"),
    USER_LOGOUT_OK(HttpStatus.OK, "USER_200", "유저 로그아웃 성공"),


    // Vote Success
    VOTE_CREATED_OK(HttpStatus.OK, "VOTE_200", "투표 생성에 성공하였습니다."),
    VOTE_RESULTS_OK(HttpStatus.OK, "VOTE_201", "투표 결과 조회에 성공하였습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ReasonDto getReason() {
        return ReasonDto.builder()
                .status(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
