package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.usecase.dto.CustomerDto;
import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;

@Service
public class CreateOrder {

    @Value("${customer.api.url}")
    private String customerApiUrl;

    private final OrderRepository repository;

    public CreateOrder(OrderRepository repository) {
        this.repository = repository;
    }

    public Order execute(CreateOrderDto createOrderDto) {
        final CustomerDto customer = this.getCustomer(createOrderDto.customerCode());

        System.out.println("Customer: " + customer);

        final Order order = Order.create(createOrderDto.description(), customer.id(), customer.name(), customer.email());
        return this.repository.save(order);
    }

    private CustomerDto getCustomer(String id) {
        final String url = customerApiUrl +"/{id}";
        try {
        return new RestTemplateBuilder()
                .build()
                .getForObject(url, CustomerDto.class, id);
        } catch (Exception e) {
            throw new CustomerNotFoundException(id);
        }
    }
}
