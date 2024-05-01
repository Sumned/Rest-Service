package com.test_task.rest_service.models.dto;

import java.util.List;

public record MultipleErrorDto(List<ErrorDTO> errors) {

}
