package com.test_task.rest_service.exceptions;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int code;
    private final String message;

    public CustomException(ErrorMessage error) {
        this.code = error.getCode();
        this.message = error.getMessage();
    }
}
