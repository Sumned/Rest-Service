package com.test_task.rest_service.services;

import com.test_task.rest_service.models.dto.UserDTO;

import java.time.LocalDate;

public interface ValidationService {
    void validateUser(UserDTO userDTO);

    void validateUserAge(LocalDate birthDate);

    void validateDates(LocalDate from, LocalDate to);
}
