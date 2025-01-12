package com.umc.yeogi_gal_lae.global.oauth.service;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.oauth.OauthAttributes;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOauth2User;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.Oauth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // registrationId (kakao, google 등)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("OAuth2 Registration ID: {}", registrationId);

        // 실제로 OAuth2 서버(kakao)에서 유저 정보(attributes)를 가져옴
        OAuth2User oAuth2User = new org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService()
                .loadUser(userRequest);
        log.debug("OAuth2 User Attributes: {}", oAuth2User.getAttributes());

        // attributes 파싱
        Oauth2UserInfo userInfo = OauthAttributes.of(registrationId, oAuth2User.getAttributes());
        log.info("Parsed OAuth2UserInfo: email={}, name={}, imageUrl={}",
                userInfo.getEmail(), userInfo.getName(), userInfo.getImageUrl());

        // DB에 사용자 저장/업데이트
        User user = saveOrUpdate(userInfo);
        log.info("User after saveOrUpdate: {}", user);

        // CustomOauth2User로 래핑하여 반환 (User 객체 포함)
        return new CustomOauth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(),
                user
        );
    }

    @Transactional
    protected User saveOrUpdate(Oauth2UserInfo userInfo) {
        String email = userInfo.getEmail();
        if (email == null || email.isEmpty()) {
            log.error("OAuth2UserInfo 이메일이 null이거나 비어 있습니다.");
            throw new IllegalArgumentException("OAuth2UserInfo 이메일이 null이거나 비어 있습니다.");
        }

        // email이 DB에 이미 있는지 확인
        var userOptional = userRepository.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            // 있으면 업데이트(예: 닉네임, 프로필 이미지)
            user = userOptional.get();
            log.info("Existing user found: {}", user);
            user.setUsername(userInfo.getName());
            user.setProfileImage(userInfo.getImageUrl());
            // 추가 업데이트 로직이 필요하면 여기에
        } else {
            // 없으면 새로 생성
            user = User.builder()
                    .email(email)
                    .username(userInfo.getName())
                    .profileImage(userInfo.getImageUrl())
                    .build();
            log.info("New user created: {}", user);
        }
        return userRepository.save(user);
    }
}
