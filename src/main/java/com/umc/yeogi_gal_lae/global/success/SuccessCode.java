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

    // Place Success
    PLACE_ADD_OK(HttpStatus.OK, "PLACE_200", "여행 장소 등록 성공."),
    PLACE_DELETED(HttpStatus.OK, "PLACE_200", "여행 장소 삭제 성공."),
    PLACES_FETCHED(HttpStatus.OK, "PLACE_201", "여행 장소 전체 조회 성공y."),

    // Vote Success
    VOTE_ROOM_CREATED_OK(HttpStatus.OK, "VOTE_200", "투표방 생성에 성공하였습니다."),
    VOTE_CREATED_OK(HttpStatus.OK, "VOTE_201", "투표 생성에 성공하였습니다."),
    VOTE_RESULTS_OK(HttpStatus.OK, "VOTE_202", "투표 결과 조회에 성공하였습니다."),
    VOTE_FINISH_OK(HttpStatus.OK, "VOTE_203", "여행 계획 확정(투표)에 성공하였습니다."),


    // Room Success
    ROOM_CREATED_OK(HttpStatus.OK, "ROOM_200", "방 생성에 성공하였습니다."),
    ROOM_DETAILS_OK(HttpStatus.OK, "ROOM_201", "방 상세 조회에 성공하였습니다."),
    ROOM_MEMBERS_OK(HttpStatus.OK, "ROOM_202", "방 멤버 조회에 성공하였습니다."),
    ROOM_MEMBERS_ADDED_OK(HttpStatus.OK, "ROOM_203", "방 멤버 추가에 성공하였습니다."),

    // Friendship Success
    FRIEND_LIST_OK(HttpStatus.OK, "FRIEND_200", "친구 목록 조회 성공."),
    FRIENDSHIP_CREATED_OK(HttpStatus.CREATED, "FRIEND_201", "친구 관계 생성 성공."),
    INVITE_CREATED_OK(HttpStatus.CREATED, "INVITE_200", "초대 생성 성공."),
    

    ROOM_MEMBERS_ADDED_OK(HttpStatus.OK, "ROOM_203", "방 멤버 추가에 성공하였습니다."), // 새로 추가된 코드

    // AI Success
    AI_TRIP_PLAN_GENERATED(HttpStatus.OK, "20001", "여행 일정이 성공적으로 생성되었습니다.");


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
