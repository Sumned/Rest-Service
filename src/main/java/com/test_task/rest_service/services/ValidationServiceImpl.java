package com.test_task.rest_service.services;

import com.test_task.rest_service.exceptions.ErrorMessage;
import com.test_task.rest_service.exceptions.ValidationException;
import com.test_task.rest_service.models.dto.UserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ValidationServiceImpl implements ValidationService {
    @Value("${user.age.min}")
    private byte minimalAge;

    @Override
    public void validateUser(UserDTO userDTO) {
        validateUserAge(userDTO.getBirthDate());
    }

    @Override
    public void validateUserAge(LocalDate birthDate) {
        long userAge = ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        if (userAge < minimalAge) {
            throw new ValidationException(ErrorMessage.SMALL_AGE);
        }
    }

    @Override
    public void validateDates(LocalDate from, LocalDate to) {
        if (to.isBefore(from)) {
            throw new ValidationException(ErrorMessage.INVALID_DATE);
        }
    }
}
