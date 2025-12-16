package com.trustai.exception;

import lombok.Getter;

/**
 * Base custom exception with error code for structured logging
 */
@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String errorType;

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorType = errorCode.name();
    }

    public CustomException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorType = errorCode.name();
    }
}

