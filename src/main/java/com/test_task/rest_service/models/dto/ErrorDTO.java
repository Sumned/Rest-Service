package com.test_task.rest_service.models.dto;

import lombok.Data;

@Data
public class ErrorDTO {
    private String title = "Bad Request";
    private String detail;

    public ErrorDTO(String detail) {
        this.detail = detail;
    }
}
