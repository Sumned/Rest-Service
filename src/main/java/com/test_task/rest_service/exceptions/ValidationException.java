package com.test_task.rest_service.exceptions;

import lombok.Getter;

@Getter
public class ValidationException extends CustomException {
    public ValidationException(ErrorMessage error) {
        super(error);
    }
}
