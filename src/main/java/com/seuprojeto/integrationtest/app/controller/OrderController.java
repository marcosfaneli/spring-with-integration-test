package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.app.usecase.CreateOrder;
import com.seuprojeto.integrationtest.app.usecase.ListOrders;
import com.seuprojeto.integrationtest.app.usecase.UpdateOrder;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
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

    private final CreateOrder createOrder;

    private final ListOrders listOrders;

    private final UpdateOrder updateOrder;

    public OrderController(
            OrderRepository repository,
            CreateOrder createOrder, ListOrders listOrders, UpdateOrder updateOrder) {
        this.repository = repository;
        this.createOrder = createOrder;
        this.listOrders = listOrders;
        this.updateOrder = updateOrder;
    }

    @GetMapping
    public List<OrderCreatedDto> getAllOrders() {
        return this.listOrders.execute().stream()
                .map(OrderCreatedDto::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedDto createOrder(@RequestBody CreateOrderDto createOrderDto) {
        return OrderCreatedDto.from(this.createOrder.execute(createOrderDto));
    }

    @PutMapping("/{id}")
    public OrderCreatedDto updateOrder(@PathVariable String id, @RequestBody UpdateOrderDto updateOrderDto) {
        return OrderCreatedDto.from(this.updateOrder.execute(id, updateOrderDto));
    }

    @GetMapping("/{id}")
    public OrderCreatedDto getOrder(@PathVariable String id) {
        final UUID uuid = getUuid(id);
        final Order order = this.repository.findById(uuid).orElseThrow(() -> new OrderNotFoundException(id));
        return OrderCreatedDto.from(order);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        final UUID uuid = getUuid(id);
        this.repository.deleteById(uuid);
    }

    private static UUID getUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(id);
        }
    }
}
