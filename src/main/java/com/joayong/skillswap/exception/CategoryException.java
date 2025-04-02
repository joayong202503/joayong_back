package com.joayong.skillswap.exception;

import lombok.Getter;

@Getter
public class CategoryException extends RuntimeException {
        private final com.joayong.skillswap.exception.ErrorCode errorCode;

    public CategoryException(com.joayong.skillswap.exception.ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CategoryException(com.joayong.skillswap.exception.ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
