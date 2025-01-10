package com.umc.yeogi_gal_lae.global.oauth.oauth2user;

import java.util.Map;
import java.util.Optional;

/**
 * OAuth2UserInfoUtil 클래스는 중첩된 맵에서 안전하게 값을 추출하는 유틸리티 메서드를 제공합니다.
 */
public class OAuth2UserInfoUtil {

    /**
     * 중첩된 맵에서 값을 안전하게 추출하는 유틸리티 메서드
     *
     * @param attributes 속성 맵
     * @param keys 탐색할 키 배열
     * @return Optional<Object> 객체
     */
    public static Optional<Object> getNestedAttribute(Map<String, Object> attributes, String... keys) {
        Object current = attributes;
        for (String key : keys) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(key);
                if (current == null) {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(current);
    }
}
