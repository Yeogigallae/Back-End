package com.umc.yeogi_gal_lae.api.friendship.controller;

import com.umc.yeogi_gal_lae.api.friendship.dto.CreateInviteRequest;
import com.umc.yeogi_gal_lae.api.friendship.dto.CreateInviteResponse;
//import com.umc.yeogi_gal_lae.api.friendship.dto.FriendListResponse;
import com.umc.yeogi_gal_lae.api.friendship.service.FriendshipService;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
@RequiredArgsConstructor
@Slf4j
public class FriendshipController {

    private final FriendshipService friendshipService;

    @Operation(
            summary = "친구 초대 URL 생성",
            description = "사용자가 친구 초대를 위해 공유 가능한 URL을 생성합니다."
    )
    @PostMapping("/invite")
    public Response<CreateInviteResponse> createInvite(@RequestBody CreateInviteRequest request) {

        String inviteUrl = friendshipService.generateInviteUrl(request.getInviterId());

        CreateInviteResponse response = new CreateInviteResponse(inviteUrl);
        return Response.of(SuccessCode.INVITE_CREATED_OK, response);
    }

    @Operation(
            summary = "친구 초대 수락",
            description = "친구 초대 URL을 통해 친구 요청을 수락하고 관계를 생성합니다."
    )
    @PostMapping("/accept")
    public Response<Void> acceptInvite(@RequestParam @NotNull(message = "토큰은 필수입니다.") String token) {

        // 친구 초대 수락자의 유저 정보
        String inviteeEmail = AuthenticatedUserUtils.getAuthenticatedUserEmail();

        friendshipService.acceptInvite(token,inviteeEmail);
        return Response.of(SuccessCode.FRIENDSHIP_CREATED_OK);
    }

    @Validated
    @Operation(
            summary = "친구 목록 조회",
            description = "사용자의 친구 목록을 반환합니다."
    )
    @GetMapping("/list/{userId}")
    public Response<List<FriendListResponse>> getFriendList(
            @PathVariable @NotNull(message = "사용자 ID는 필수입니다.") Long userId) {

        List<FriendListResponse> friends = friendshipService.getFriendList(userId);

        return Response.of(SuccessCode.FRIEND_LIST_OK, friends);
    }
}