package com.test_task.rest_service.exceptions;

public class UserException extends CustomException {
    public UserException(ErrorMessage error) {
        super(error);
    }
}
