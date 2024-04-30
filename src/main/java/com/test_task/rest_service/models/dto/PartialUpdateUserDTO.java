package com.test_task.rest_service.models.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.test_task.rest_service.Constants.*;

@Getter
public class PartialUpdateUserDTO {
    @Email(message = EMAIL_MESSAGE,
            regexp = EMAIL_PATTERN)
    private String email;
    private String firstName;
    private String lastName;

    @DateTimeFormat(pattern = DATE_PATTERN)
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
}
