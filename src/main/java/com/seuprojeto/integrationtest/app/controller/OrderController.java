package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<?> getAllOrders() {
        return List.of();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        final Order order = Order.create(createOrderDto.description());
        final Order orderSaved = this.repository.save(order);
        return OrderCreatedDto.from(orderSaved);
    }
}
