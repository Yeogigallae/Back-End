package com.umc.yeogi_gal_lae.global.error;

import com.umc.yeogi_gal_lae.global.common.response.ReasonDto;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseStatus {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러입니다. 관리자에게 문의하세요."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "찾을 수 없는 요청입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 메소드입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    INPUT_VALUE_INVALID(HttpStatus.BAD_REQUEST, "REQUEST_400", "요청사항에 필수 인자가 누락되었습니다"),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "G005", "request message body가 없거나, 값 타입이 올바르지 않습니다."),

    // User Error
    USER_NOT_FOUND(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 정보가 없습니다."),
    USER_NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, "USER_401", "로그인 하지 않았습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "USER_403", "권한이 없습니다."),

    // Token Error
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN_401", "리프레시 토큰이 유효하지 않습니다."),

    // Place Error
    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_404", "장소를 찾을 수 없습니다."),

    // Room Error
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROOM_404", "룸을 찾을 수 없습니다."),

    INVALID_PLACE_FOR_ROOM(HttpStatus.BAD_REQUEST, "ROOM_400", "룸에 속하지 않은 장소입니다."),

    // 클라이언트 오류 코드
    PLACE_NOT_BELONG_TO_ROOM(HttpStatus.BAD_REQUEST, "40001", "해당 장소는 요청한 방에 속하지 않습니다."),
    NO_PLACES_FOUND(HttpStatus.BAD_REQUEST, "40002", "해당 방에 속한 장소가 없습니다."),

    // 서버 오류 코드
    AI_TRIP_PLAN_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "50001", "여행 일정 생성에 실패했습니다.");;

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
