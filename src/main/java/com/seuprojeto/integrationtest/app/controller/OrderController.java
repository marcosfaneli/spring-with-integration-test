package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.app.usecase.*;
import com.seuprojeto.integrationtest.integration.app.controller.dto.UpdateOrderDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final CreateOrder createOrder;

    private final ListOrders listOrders;

    private final UpdateOrder updateOrder;

    private final GetOrderById getOrderById;

    private final DeleteOrder deleteOrder;

    public OrderController(
            CreateOrder createOrder,
            ListOrders listOrders,
            UpdateOrder updateOrder,
            GetOrderById getOrderById,
            DeleteOrder deleteOrder) {
        this.createOrder = createOrder;
        this.listOrders = listOrders;
        this.updateOrder = updateOrder;
        this.getOrderById = getOrderById;
        this.deleteOrder = deleteOrder;
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
        return OrderCreatedDto.from(this.getOrderById.execute(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        this.deleteOrder.execute(id);
    }

}
