package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteOrder {

    private final OrderRepository repository;

    public DeleteOrder(OrderRepository repository) {
        this.repository = repository;
    }

    public void execute(String id) {
        final UUID uuid = UUIDConverter.fromIdToUuid(id);
        this.repository.deleteById(uuid);
    }
}
