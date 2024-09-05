package com.seuprojeto.integrationtest.fixture;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;

public class CreateOrderFixture {

    public static CreateOrderDto createOrderDto() {
        return new CreateOrderDto("description", "customerCode");
    }

    public static CreateOrderDto createOrderDto(String description, String customerCode) {
        return new CreateOrderDto(description, customerCode);
    }
}
