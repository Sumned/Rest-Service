package com.test_task.rest_service.models.dto;

import lombok.Data;

@Data
public class ErrorDTO {
    private String title = "Bad Request";
    private int status;
    private String link;
    private String detail;

    public ErrorDTO(int status, String link, String detail) {
        this.status = status;
        this.link = link;
        this.detail = detail;
    }
}
