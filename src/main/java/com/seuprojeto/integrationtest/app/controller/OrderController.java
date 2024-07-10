package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import com.seuprojeto.integrationtest.integration.app.controller.dto.UpdateOrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<OrderCreatedDto> getAllOrders() {
        return this.repository.findAll().stream()
                .map(OrderCreatedDto::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        final Order order = Order.create(createOrderDto.description());
        final Order orderSaved = this.repository.save(order);
        return OrderCreatedDto.from(orderSaved);
    }

    @PutMapping("/{id}")
    public OrderCreatedDto updateOrder(@PathVariable String id, @RequestBody UpdateOrderDto updateOrderDto) {
        final UUID uuid = UUID.fromString(id);
        final Order order = this.repository.findById(uuid).orElseThrow();
        order.update(updateOrderDto.description(), updateOrderDto.status());
        final Order orderSaved = this.repository.save(order);
        return OrderCreatedDto.from(orderSaved);
    }

    @GetMapping("/{id}")
    public OrderCreatedDto getOrder(@PathVariable String id) {
        final UUID uuid = UUID.fromString(id);
        final Order order = this.repository.findById(uuid).orElseThrow();
        return OrderCreatedDto.from(order);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        final UUID uuid = UUID.fromString(id);
        this.repository.deleteById(uuid);
    }
}
