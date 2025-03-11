package com.joayong.skillswap.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateUserException extends RuntimeException{
    private final HttpStatus status;

    public DuplicateUserException(String message){
        super(message);
        this.status = HttpStatus.CONFLICT;
    }

}
