package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetOrderById {

    private final OrderRepository repository;

    public GetOrderById(OrderRepository repository) {
        this.repository = repository;
    }

    public Order execute(String id) {
        final UUID uuid = UUIDConverter.fromIdToUuid(id);
        return this.repository.findById(uuid).orElseThrow(() -> new OrderNotFoundException(id));
    }
}
