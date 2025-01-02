package com.umc.yeogi_gal_lae.global.exception;

import com.umc.yeogi_gal_lae.global.response.Code;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final Code code;

    public BusinessException(Code code) {
        super(code.getMessage());
        this.code = code;
    }
}
