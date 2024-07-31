package com.seuprojeto.integrationtest.app.controller.dto;


import com.seuprojeto.integrationtest.domain.Order;

public record OrderCreatedDto(String id, String description, String status, String createdAt, String updatedAt, String customerName, String customerCode, String email) {
    public static OrderCreatedDto from(Order order) {
        return new OrderCreatedDto(
                order.getId().toString(),
                order.getDescription(),
                order.getStatus().name(),
                order.getCreatedAt().toString(),
                order.getUpdatedAt().toString(),
                order.getCustomer().getName(),
                order.getCustomer().getId(),
                order.getCustomer().getEmail());
    }
}
