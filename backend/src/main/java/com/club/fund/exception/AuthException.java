package com.club.fund.exception;

import com.club.fund.common.ErrorCode;
import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {

    private final Integer code;

    public AuthException(String message) {
        super(message);
        this.code = 401;
    }

    public AuthException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }
}
