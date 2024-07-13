package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import com.seuprojeto.integrationtest.integration.app.controller.dto.UpdateOrderDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateOrder {

    private final OrderRepository repository;

    public UpdateOrder(OrderRepository repository) {
        this.repository = repository;
    }

    public Order execute(String id, UpdateOrderDto updateOrderDto) {
        final UUID uuid = UUIDConverter.fromIdToUuid(id);
        final Order order = this.repository.findById(uuid).orElseThrow(() -> new OrderNotFoundException(id));
        order.update(updateOrderDto.description(), updateOrderDto.status());

        return this.repository.save(order);
    }
}
