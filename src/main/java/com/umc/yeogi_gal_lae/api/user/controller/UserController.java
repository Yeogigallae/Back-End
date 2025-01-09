package com.umc.yeogi_gal_lae.api.user.controller;

import static com.umc.yeogi_gal_lae.global.response.Code.USER_FETCH_OK;

import com.umc.yeogi_gal_lae.api.user.converter.UserConverter;
import com.umc.yeogi_gal_lae.api.user.dto.response.UserInfoResponse;
import com.umc.yeogi_gal_lae.api.user.service.UserService;
import com.umc.yeogi_gal_lae.global.response.Code;
import com.umc.yeogi_gal_lae.global.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @Operation(summary = "유저 정보")
    @GetMapping("/user")
    public Response<UserInfoResponse> getUserInfo() {
        UserInfoResponse data = UserConverter.ofUserInfoResponse(userService.getUser());
        return Response.ok(USER_FETCH_OK, data);
    }

}
