package com.umc.yeogi_gal_lae.api.user.controller;

import com.umc.yeogi_gal_lae.api.user.converter.UserConverter;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserInfoResponse;
import com.umc.yeogi_gal_lae.api.user.service.UserService;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 도메인 관련 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/user")
    public Response<UserInfoResponse> getUserInfo() {
        // userService.getUser() -> 현재 로그인한 User 엔티티 반환
        var user = userService.getUser();
        // User -> UserInfoResponse 로 변환
        UserInfoResponse result = UserConverter.ofUserInfoResponse(user);
        return Response.of(SuccessCode.USER_FETCH_OK, result);
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/reissue")
    public Response<Void> reGenerateAccessToken(@RequestHeader("Access-Token") String accessToken,
                                                @RequestHeader("Refresh-Token") String refreshToken,
                                                HttpServletResponse response) {
        // 만료된 Access-Token + 유효한 Refresh-Token으로 새 토큰 발급
        JwtToken result = userService.reissueToken(accessToken, refreshToken);

        // 새로 발급된 토큰을 헤더에 담아 응답
        response.setHeader("Access-Token", result.getAccessToken());
        response.setHeader("Refresh-Token", result.getRefreshToken());

        return Response.of(SuccessCode.TOKEN_REISSUE_OK, null);
    }

    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext(); // 인증 정보 제거
        request.getSession().invalidate(); // 세션 무효화

        response.setHeader("Set-Cookie", "XSRF-TOKEN=; HttpOnly; Path=/; Max-Age=0");

        return Response.of(SuccessCode.OK);
    }

}

