package com.umc.yeogi_gal_lae.global.oauth.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.api.user.service.UserService;
import com.umc.yeogi_gal_lae.global.oauth.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.*;
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
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 1) 기본 OAuth2UserService
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 2) registrationId, userNameAttributeName
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 3) attributes
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 4) 파싱
        OAuthAttributes oAuthAttrs = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        // 5) 이메일
        String email = oAuthAttrs.getEmail();
        log.info("소셜 로그인 registrationId={}, email={}", registrationId, email);

        // 6) DB 저장
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("새로운 소셜 로그인 사용자 생성: {}", email);
                    return userService.createUser(oAuthAttrs, email);
                });

        // 7) 반환 (Spring Security가 OAuth2User로 사용)
        return oAuth2User;
    }
}
