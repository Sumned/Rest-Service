package com.test_task.rest_service.controller.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.test_task.rest_service.exceptions.CustomException;
import com.test_task.rest_service.exceptions.UserException;
import com.test_task.rest_service.exceptions.ValidationException;
import com.test_task.rest_service.models.dto.PartialUpdateUserDTO;
import com.test_task.rest_service.models.dto.UserDTO;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String LOCAL_DATE = "LocalDate";
    private static final String BAD_DATE_DRAFT = "Date %s is incorrect";

    @ExceptionHandler(value = {UserException.class, ValidationException.class})
    protected ResponseEntity<String> handleServiceExceptions(CustomException customException) {
        HttpStatus status;
        if (customException.getCode() == 0) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(customException.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains(LOCAL_DATE)) {
            if (ex.getCause() instanceof InvalidFormatException) {
                String badValue = ((InvalidFormatException) ex.getCause()).getValue().toString();
                return ResponseEntity.status(status).body(String.format(BAD_DATE_DRAFT, badValue));
            }
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        String objName = bindingResult.getObjectName();
        if (UserDTO.class.getSimpleName().equalsIgnoreCase(objName) || PartialUpdateUserDTO.class.getSimpleName().equalsIgnoreCase(objName)) {
            StringBuilder builder = new StringBuilder();
            bindingResult.getAllErrors().forEach(e -> builder.append(builder.length() > 0 ? ",\n" + e.getDefaultMessage() : e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(builder.toString());
        }
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains(LOCAL_DATE) && ex.getValue() != null) {
            String badValue = ex.getValue().toString();
            return ResponseEntity.status(status).body(String.format(BAD_DATE_DRAFT, badValue));
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }
}
