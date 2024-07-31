package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.service.CustomerService;
import com.seuprojeto.integrationtest.domain.Customer;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrder {

    private final OrderRepository orderRepository;

    private final CustomerService customerService;

    public CreateOrder(OrderRepository orderRepository, CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    public Order execute(CreateOrderDto createOrderDto) {

        final Customer customerSaved = this.customerService.findById(createOrderDto.customerCode());

        final Order order = Order.create(createOrderDto.description(), customerSaved);

        return this.orderRepository.save(order);
    }
}
