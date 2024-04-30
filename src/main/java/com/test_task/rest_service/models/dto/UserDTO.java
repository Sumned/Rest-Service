package com.test_task.rest_service.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static com.test_task.rest_service.Constants.*;

@Getter
public class UserDTO {
    @NotBlank(message = "Email is mandatory")
    @Email(message = EMAIL_MESSAGE,
            regexp = EMAIL_PATTERN)
    private String email;

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Birth date is mandatory")
    @DateTimeFormat(pattern = DATE_PATTERN)
    private LocalDate birthDate;
    private String phoneNumber;
    private String address;
}
