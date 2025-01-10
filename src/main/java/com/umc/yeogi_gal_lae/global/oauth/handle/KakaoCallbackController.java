package com.umc.yeogi_gal_lae.global.oauth.handle;

import com.umc.yeogi_gal_lae.api.user.domain.User;
import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.oauth2user.CustomOAuth2User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class KakaoCallbackController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

//    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
//    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    @Value("${jwt.secure}")
    private boolean secure;

    @GetMapping("/login/kakaoCallback")
    public void kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        log.info("Kakao OAuth2 Callback received with code: {}", code);

        try {
            // 1. 인가 코드를 사용하여 Access Token 요청
            String accessToken = getKakaoAccessToken(code);

            // 2. Access Token을 사용하여 사용자 정보 조회
            CustomOAuth2User oAuth2User = getKakaoUserInfo(accessToken);

            // 3. 사용자 정보 기반으로 JWT 토큰 생성
            String appAccessToken = jwtService.createAccessToken(oAuth2User.getEmail());
            String appRefreshToken = jwtService.createRefreshToken();

            // 4. 데이터베이스에 사용자 정보 저장 또는 업데이트
            User user = userRepository.findByEmail(oAuth2User.getEmail())
                    .orElseGet(() -> {
                        // 신규 사용자 생성
                        User newUser = User.builder()
                                .email(oAuth2User.getEmail())
                                .profileImage(oAuth2User.getProfileImage())
                                .refreshToken(appRefreshToken)
                                .build();
                        return userRepository.save(newUser);
                    });

            // 기존 사용자의 Refresh Token 업데이트
            if (user.getRefreshToken() != null) {
                user.updateRefreshToken(appRefreshToken);
                userRepository.saveAndFlush(user);
            }

            // 5. JWT 토큰을 쿠키에 설정
            addTokenCookies(response, appAccessToken, appRefreshToken);

            // 6. 프론트엔드로 리다이렉트
            String redirectUrl = "http://localhost:5173"; // 프론트엔드 URL로 변경
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("Kakao OAuth2 Callback 처리 중 오류 발생: ", e);
            // 에러 페이지로 리다이렉트하거나, 적절한 오류 처리 수행
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "OAuth2 Callback 처리 중 오류 발생");
            } catch (IOException ioException) {
                log.error("Error sending error response", ioException);
            }
        }
    }

    /**
     * 인가 코드를 사용하여 Kakao로부터 Access Token을 획득합니다.
     *
     * @param code 인가 코드
     * @return Kakao Access Token
     * @throws IOException
     */
    private String getKakaoAccessToken(String code) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        //params.add("client_secret", clientSecret); // Kakao는 client_secret을 요구하지 않음

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode node = objectMapper.readTree(response.getBody());
            return node.get("access_token").asText();
        } else {
            throw new OAuth2AuthenticationException("Failed to obtain Kakao access token");
        }
    }

    /**
     * Access Token을 사용하여 Kakao로부터 사용자 정보를 조회합니다.
     *
     * @param accessToken Kakao Access Token
     * @return 사용자 정보
     * @throws IOException
     */
    private CustomOAuth2User getKakaoUserInfo(String accessToken) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                entity,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            JsonNode node = objectMapper.readTree(response.getBody());

            String email = node.path("kakao_account").path("email").asText();
            String profileImage = node.path("properties").path("profile_image").asText();

            return CustomOAuth2User.builder()
                    .email(email)
                    .profileImage(profileImage)
                    .build();
        } else {
            throw new RuntimeException("Failed to obtain Kakao user info");
        }
    }

    /**
     * Access Token과 Refresh Token을 쿠키에 설정합니다.
     *
     * @param response     HTTP 응답 객체
     * @param accessToken  생성된 Access Token
     * @param refreshToken 생성된 Refresh Token
     */
    private void addTokenCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        log.info("Setting AccessToken and RefreshToken cookies");

        // Access Token 쿠키 생성
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(secure); // 환경에 따라 설정
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(60 * 60); // 1시간

        // Refresh Token 쿠키 생성
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(secure); // 환경에 따라 설정
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일

        // 응답에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
