package com.test_task.rest_service.exceptions;

import lombok.Getter;

@Getter
public enum ErrorMessage {
    USER_NOT_EXIST(0, "User with this id doesn't exist"),
    EMAIL_ALREADY_USED(1, "This email address is already in use"),
    SMALL_AGE(2, "Age is less than the minimum allowed"),
    INVALID_DATE(3, "'From' date must be before 'to' date");

    private final int code;
    private final String message;

    ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
