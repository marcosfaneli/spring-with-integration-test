package com.seuprojeto.integrationtest.app.controller;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.controller.dto.OrderCreatedDto;
import com.seuprojeto.integrationtest.app.controller.dto.PaginatedResponseDto;
import com.seuprojeto.integrationtest.app.controller.dto.UpdateOrderDto;
import com.seuprojeto.integrationtest.app.usecase.*;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public PaginatedResponseDto<OrderCreatedDto> getAllOrders(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String query) {
        final var orders = this.listOrders.execute(query, pageable);
        final List<OrderCreatedDto> ordersDto = orders.map(OrderCreatedDto::from).getContent();
        return new PaginatedResponseDto<>(orders.getNumber(), orders.getSize(), orders.getTotalElements(), ordersDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedDto createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        return OrderCreatedDto.from(this.createOrder.execute(createOrderDto));
    }

    @PutMapping("/{id}")
    public OrderCreatedDto updateOrder(@PathVariable String id, @RequestBody UpdateOrderDto updateOrderDto) {
        return OrderCreatedDto.from(this.updateOrder.execute(getUuid(id), updateOrderDto));
    }

    @GetMapping("/{id}")
    public OrderCreatedDto getOrder(@PathVariable String id) {
        return OrderCreatedDto.from(this.getOrderById.execute(getUuid(id)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        this.deleteOrder.execute(getUuid(id));
    }

    private static UUID getUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new OrderNotFoundException(id);
        }
    }
}
