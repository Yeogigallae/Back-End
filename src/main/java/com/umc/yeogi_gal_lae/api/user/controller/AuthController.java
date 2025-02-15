package com.umc.yeogi_gal_lae.api.user.controller;

import com.umc.yeogi_gal_lae.api.user.converter.UserConverter;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserResponseDTO.JoinResultDTO;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.user.service.AuthService;
import com.umc.yeogi_gal_lae.api.vote.AuthenticatedUserUtils;
import com.umc.yeogi_gal_lae.global.common.response.BaseResponse;
import com.umc.yeogi_gal_lae.global.common.response.Response;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.oauth.util.CookieUtil;
import com.umc.yeogi_gal_lae.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @GetMapping("/login/kakao")
    public BaseResponse<JoinResultDTO> kakaoLogin(@RequestParam("code") String accessCode, String redirectUri, HttpServletResponse httpServletResponse) {
        User user = authService.oAuthLogin(accessCode, redirectUri, httpServletResponse);
        return BaseResponse.onSuccess(UserConverter.toJoinResultDTO(user));
    }

    @PostMapping("/logout")
    public BaseResponse<String> logout(HttpServletResponse response) {
        String email = AuthenticatedUserUtils.getAuthenticatedUserEmail();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Refresh Token 삭제하여 재발급 방지
        user.setRefreshToken(null);
        userRepository.save(user);

        // 쿠키 삭제 (쿠키 만료시간을 0으로 설정)
        CookieUtil.deleteCookie(response, "accessToken");
        CookieUtil.deleteCookie(response, "refreshToken");

        // SecurityContext 초기화
        SecurityContextHolder.clearContext();

        return BaseResponse.onSuccess("로그아웃 성공");
    }

    @DeleteMapping("/delete")
    public BaseResponse<String> deleteUser(HttpServletResponse response) {
        return authService.deleteUser(response);
    }

    @Operation(summary = "유저 정보 조회")
    @GetMapping("/user")
    public Response<JoinResultDTO> getUserInfo() {
        // userService.getUser() -> 현재 로그인한 User 엔티티 반환
        var user = authService.getUser();
        // User -> UserInfoResponse 로 변환
        JoinResultDTO result = UserConverter.toJoinResultDTO(user);
        return Response.of(SuccessCode.USER_FETCH_OK, result);
    }
}