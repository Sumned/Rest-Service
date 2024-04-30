package com.test_task.rest_service.models.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class PartialUpdateUserDTO {
    @Email(message = "Please provide correct email", regexp = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$")
    private String email;
    private String firstName;
    private String lastName;
    @Pattern(message = "Please provide correct date in the format yyyy-mm-dd", regexp = "[1-2]\\d{3}-[0-1]\\d-[0-3]\\d")
    private String birthDate;
    private String phoneNumber;
    private String address;
}