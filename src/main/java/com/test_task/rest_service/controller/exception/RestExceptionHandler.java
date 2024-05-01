package com.test_task.rest_service.controller.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.test_task.rest_service.exceptions.CustomException;
import com.test_task.rest_service.exceptions.UserException;
import com.test_task.rest_service.exceptions.ValidationException;
import com.test_task.rest_service.models.dto.ErrorDTO;
import com.test_task.rest_service.models.dto.MultipleErrorDto;
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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    private static final String LOCAL_DATE = "LocalDate";
    private static final String BAD_DATE_DRAFT = "Date %s is incorrect";

    @ExceptionHandler(value = {UserException.class, ValidationException.class})
    protected ResponseEntity<ErrorDTO> handleServiceExceptions(CustomException customException, WebRequest request) {
        HttpStatus status;
        if (customException.getCode() == 0) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(new ErrorDTO(status.value(), getUri(request), customException.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains(LOCAL_DATE)) {
            if (ex.getCause() instanceof InvalidFormatException) {
                String badValue = ((InvalidFormatException) ex.getCause()).getValue().toString();
                return handleExceptionInternal(ex, new ErrorDTO(status.value(), getUri(request), String.format(BAD_DATE_DRAFT, badValue)),
                        headers, status, request);
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
            List<ErrorDTO> errors = new ArrayList<>();
            bindingResult.getAllErrors().forEach(e -> errors.add(new ErrorDTO(status.value(), getUri(request), e.getDefaultMessage())));
            if (errors.size() == 1) {
                return handleExceptionInternal(ex, errors.get(0),
                        headers, status, request);
            }
            return handleExceptionInternal(ex, new MultipleErrorDto(errors),
                    headers, status, request);
        }
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @Override
    @Nullable
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (ex.getMessage() != null && ex.getMessage().contains(LOCAL_DATE) && ex.getValue() != null) {
            String badValue = ex.getValue().toString();
            return handleExceptionInternal(ex, new ErrorDTO(status.value(),
                            getUri(request),
                            String.format(BAD_DATE_DRAFT, badValue)),
                    headers, status, request);
        }
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    private String getUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}
