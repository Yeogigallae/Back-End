package com.umc.yeogi_gal_lae.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.umc.yeogi_gal_lae.global.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@Builder
@JsonPropertyOrder({"httpStatus", "code", "message", "result"})
public class Response<T> {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    public static <T> Response<T> of(BaseStatus status, T result) {
        return new Response<>(status.getReason().getStatus(),
                status.getReason().getCode(),
                status.getReason().getMessage(),
                result);
    }
}
