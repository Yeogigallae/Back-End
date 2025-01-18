package com.umc.yeogi_gal_lae.global.config;

import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.JwtAuthenticationFilter;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginFailureHandler;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginSuccessHandler;
import com.umc.yeogi_gal_lae.global.oauth.service.CustomOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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

        // Define the paths that should be excluded from JwtAuthenticationFilter
        String[] excludedPaths = {
                "/h2-console/**", // H2 Console
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/oauth2/**",
                "/oauth2/authorization/**",
                "/login/**",
                "/signup/**"
        };

        http
                // CSRF 비활성화 (H2 콘솔 사용을 위해)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/vote/**")
                        .ignoringRequestMatchers("/h2-console/**")
                        .ignoringRequestMatchers((request ->
                                "POST".equalsIgnoreCase(request.getMethod()) ||
                                "PUT".equalsIgnoreCase(request.getMethod()) ||
                                "DELETE".equalsIgnoreCase(request.getMethod()))
                        )
                )
                // CORS 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 세션 사용 안 함 (STATELESS)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 프레임 옵션 설정 (H2 콘솔 사용을 위해)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )
                // HTTP Basic 인증 비활성
                .httpBasic(AbstractHttpConfigurer::disable)
                // URL 접근 정책 설정
                .authorizeHttpRequests(auth -> auth
                        // H2 콘솔 및 Swagger 등 허용
                        .requestMatchers(excludedPaths).permitAll()
                        // 그 외 /api/** 경로는 인증 필요
                        .requestMatchers("/api/**").authenticated()
                        // 기타 모든 요청은 허용 (필요시 수정)
                        .anyRequest().permitAll()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(endpoint -> endpoint.userService(customOauth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                );

        // JwtAuthenticationFilter를 Jwt 필터로 등록하되, excludedPaths는 제외
        http.addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        // Define paths to exclude from the filter
        List<String> excludeUrlPatterns = List.of("/h2-console/**");

        return new JwtAuthenticationFilter(jwtService, userRepository, excludeUrlPatterns);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Refresh-Token", "Content-Disposition", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
