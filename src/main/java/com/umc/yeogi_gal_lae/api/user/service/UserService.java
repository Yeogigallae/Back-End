package com.umc.yeogi_gal_lae.api.user.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.error.BusinessException;
import com.umc.yeogi_gal_lae.global.error.ErrorCode;
import com.umc.yeogi_gal_lae.global.jwt.JwtToken;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * 현재 로그인한 사용자 정보를 가져온다고 가정한 예시 메서드 - 실제로는 SecurityContext에서 email 을 꺼내거나,
     *
     * @AuthenticationPrincipal UserDetails 를 이용하는 등 다양하게 구현 가능
     */
    public User getUser() {
        // SecurityContextHolder에서 이메일 가져오기
        String currentUserEmail = jwtService.getCurrentUserEmail()
                .orElseThrow(() -> {
                    log.error("현재 인증된 사용자 이메일을 가져오지 못했습니다.");
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        log.info("현재 인증된 사용자 이메일: {}", currentUserEmail);

        // 이메일로 유저 조회
        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> {
                    log.error("이메일 {}에 해당하는 사용자를 찾을 수 없습니다.", currentUserEmail);
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });
    }


    /**
     * AccessToken 재발급
     *
     * @param accessToken  만료된(또는 만료 직전) AccessToken
     * @param refreshToken 유효한 RefreshToken
     * @return 새롭게 발급된 accessToken, refreshToken
     */
    public JwtToken reissueToken(String accessToken, String refreshToken) {
        // 1) refreshToken 이 유효한지 체크 (jwt 만료 여부)
        if (!jwtService.validateToken(refreshToken)) {
            log.error("[UserService] Refresh Token is invalid or expired");
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN); // 예시
        }

        // 2) refreshToken 에서 email 추출
        String email = jwtService.getEmailFromToken(refreshToken);
        if (email == null) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3) DB에서 유저 조회 & 저장된 refreshToken 과 일치하는지 확인
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        User user = userOptional.get();

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4) 새 토큰 발급
        JwtToken newTokens = jwtService.createJwtToken(email);

        // 5) DB 업데이트 (Access/Refresh 토큰 최신화)
        user.setAccessToken(newTokens.getAccessToken());
        user.setRefreshToken(newTokens.getRefreshToken());
        userRepository.save(user);

        return newTokens;
    }
}
