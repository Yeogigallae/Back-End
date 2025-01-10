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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // Default OAuth2UserService를 사용하여 OAuth2User 로드
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // OAuth2 로그인 시 키(PK)가 되는 값 (카카오의 경우 "id")
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, attributes);

        // 이메일 추출
        String email = oAuthAttributes.getEmail();
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("이메일 정보가 누락되었습니다.");
        }

        log.info("소셜 로그인 사용자 이메일: {}", email);

        // 사용자 조회 및 생성 (존재하지 않으면 생성)
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("새로운 소셜 로그인 사용자 생성: {}", email);
                    return userService.createUser(oAuthAttributes, email);
                });

        // CustomOAuth2User 객체 생성 및 반환 (socialType 제거)
        return CustomOAuth2User.builder()
                .authorities(Collections.emptyList())
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .name(oAuthAttributes.getName())
                .email(email)
                .build();
    }
}
