package com.umc.yeogi_gal_lae.global.oauth.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.user.service.UserService;
import com.umc.yeogi_gal_lae.global.oauth.OAuthAttributes;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1) Spring Security에서 기본 User 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) registrationId, userNameAttributeName
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // "kakao", "google"
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // "id" or "sub"

        // 3) attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 4) OAuthAttributes 파싱
        OAuthAttributes oAuthAttrs = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        // 5) 이메일 null 체크
        String email = oAuthAttrs.getEmail();
        if (email == null || email.isEmpty()) {
            OAuth2Error oauth2Error = new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_REQUEST,
                    "이메일 정보가 누락되었습니다.",
                    null
            );
            throw new OAuth2AuthenticationException(oauth2Error, "이메일 정보가 누락되었습니다.");
        }

        log.info("소셜 로그인 Provider: {}", registrationId);
        log.info("소셜 로그인 사용자 email={} nickname={} profileImage={}",
                email, oAuthAttrs.getName(), oAuthAttrs.getProfileImage());

        // 6) DB 조회 or 새로 저장
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("새로운 소셜 로그인 사용자 생성: {}", email);
                    // userService.createUser(...) 내부에서 save(...) 후 로그 찍힘
                    return userService.createUser(oAuthAttrs, email);
                });

        // 7) CustomOAuth2User 생성
        return CustomOAuth2User.builder()
                .oAuth2User(oAuth2User)
                .email(email)
                .nickname(oAuthAttrs.getName())
                .profileImage(oAuthAttrs.getProfileImage())
                .authorities(oAuth2User.getAuthorities()) // Builder에 authorities 필드 있어야 오류X
                .build();
    }
}