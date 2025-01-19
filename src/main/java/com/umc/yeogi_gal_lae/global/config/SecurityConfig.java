package com.umc.yeogi_gal_lae.global.config;

import com.umc.yeogi_gal_lae.api.user.repository.UserRepository;
import com.umc.yeogi_gal_lae.global.jwt.JwtAuthenticationFilter;
import com.umc.yeogi_gal_lae.global.jwt.service.JwtService;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginFailureHandler;
import com.umc.yeogi_gal_lae.global.oauth.handle.Oauth2LoginSuccessHandler;
import com.umc.yeogi_gal_lae.global.oauth.service.CustomOauth2UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
//          .authorizeHttpRequests(authorize -> authorize
//                  .requestMatchers(
//                          "/v3/api-docs/**",
//                          "/oauth2/**",
//                          "/oauth2/authorization/google",
//                          "/index.html",
//                          "/swagger/**",
//                          "/swagger-ui/**",
//                          "/swagger-ui/index.html/**",
//                          "/api-docs/**",
//                          "/signup.html",
//                          "/api/v1/reissue"
//                  ).permitAll()
//                  .anyRequest().authenticated()
//          )
                // 로그아웃 성공 시 / 주소로 이동
//          .logout((logoutConfig) -> logoutConfig.logoutSuccessUrl("/"))
                .oauth2Login(oauth2Login -> oauth2Login
                        .userInfoEndpoint(endpoint -> endpoint
                                .userService(customOauth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler) // 2.
                        .failureHandler(oAuth2LoginFailureHandler) // 3.
                )
//          .exceptionHandling(authenticationManager ->authenticationManager
//                  .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                  .accessDeniedHandler(jwtAccessDeniedHandler)
//          )
                .addFilterBefore(new JwtAuthenticationFilter(jwtService, userRepository),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:8080", "http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public RedirectStrategy redirectStrategy() {
        return new DefaultRedirectStrategy(); // 기본 리다이렉트 전략 사용
    }
}
