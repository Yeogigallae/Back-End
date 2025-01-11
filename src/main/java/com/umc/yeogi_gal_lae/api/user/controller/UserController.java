package com.umc.yeogi_gal_lae.api.user.controller;

import static com.umc.yeogi_gal_lae.global.response.Code.USER_FETCH_OK;
import static com.umc.yeogi_gal_lae.global.response.Code.TOKEN_REISSUE_OK;
import static com.umc.yeogi_gal_lae.global.response.Code.USER_NOT_FOUND;

import com.umc.yeogi_gal_lae.api.user.converter.UserConverter;
import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserInfoResponse;
import com.umc.yeogi_gal_lae.api.user.service.UserService;
import com.umc.yeogi_gal_lae.global.exception.BusinessException;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.response.Code;
import com.umc.yeogi_gal_lae.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보")
    @GetMapping("/user")
    public Response<UserInfoResponse> getUserInfo() {
        UserInfoResponse result = UserConverter.ofUserInfoResponse(userService.getUser());
        return Response.of(USER_FETCH_OK, result);
    }

    @Operation(summary = "액세스 토큰 재발급")
    @PostMapping("/reissue")
    public Response<Void> reGenerateAccessToken(@RequestHeader("Access-Token") String accessToken,
                                                @RequestHeader("Refresh-Token") String refreshToken,
                                                HttpServletResponse response) {
        JwtToken result = userService.reissueToken(accessToken, refreshToken);
        response.setHeader("Access-Token", result.getAccessToken());
        response.setHeader("Refresh-Token", result.getRefreshToken());
        return Response.of(TOKEN_REISSUE_OK, null);
    }

}
