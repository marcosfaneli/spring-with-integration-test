package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.database.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListOrders {

    private final OrderRepository repository;

    public ListOrders(OrderRepository repository) {
        this.repository = repository;
    }

    public Page<Order> execute(String query, Pageable pageable) {
        if (query != null) {
            return this.repository.findByDescriptionContaining(query, pageable);
        }
        return this.repository.findAll(pageable);
    }
}
