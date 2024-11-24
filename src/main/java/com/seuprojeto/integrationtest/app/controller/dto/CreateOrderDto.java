package com.seuprojeto.integrationtest.app.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record CreateOrderDto(
        @NotEmpty(message = "Description is required") String description,
        @NotEmpty(message = "CustomerCode is required") String customerCode) {
}
