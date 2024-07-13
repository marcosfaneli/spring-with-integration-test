package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListOrders {

    private final OrderRepository repository;

    public ListOrders(OrderRepository repository) {
        this.repository = repository;
    }

    public List<Order> execute() {
        return this.repository.findAll();
    }
}
