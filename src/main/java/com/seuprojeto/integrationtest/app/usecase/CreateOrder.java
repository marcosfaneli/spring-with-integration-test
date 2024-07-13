package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrder {

    private final OrderRepository repository;

    public CreateOrder(OrderRepository repository) {
        this.repository = repository;
    }

    public Order execute(CreateOrderDto createOrderDto) {
        final Order order = Order.create(createOrderDto.description());
        return this.repository.save(order);
    }
}
