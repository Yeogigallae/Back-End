package com.umc.yeogi_gal_lae.global.config;

import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.JwtAuthenticationFilter;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginFailureHandler;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginSuccessHandler;
import com.umc.yeogi_gal_lae.global.oauth.service.CustomOauth2UserService;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final CustomOauth2UserService customOauth2UserService;
    private final Oauth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final Oauth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .httpBasic(HttpBasicConfigurer::disable)
                .sessionManagement(configurer -> configurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(requests ->
                        requests.anyRequest().permitAll() // 모든 요청을 모든 사용자에게 허용
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // 로그아웃 요청 경로 설정
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext(); // 인증 정보 제거
                            response.setStatus(HttpServletResponse.SC_OK); // 200 응답 반환
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                            response.getWriter().flush();
                        })
                        .invalidateHttpSession(true) // 세션 무효화
                        .clearAuthentication(true) // 인증 정보 제거
                        .deleteCookies("JSESSIONID") // 쿠키 제거
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS) //  세션 유지 설정
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(customOauth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler) // 로그인 성공 핸들러 연결
                        .failureHandler(oAuth2LoginFailureHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RedirectStrategy redirectStrategy() {
        return new DefaultRedirectStrategy(); // 기본 리다이렉트 전략 사용
    }
}