package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.app.controller.dto.CreateOrderDto;
import com.seuprojeto.integrationtest.app.usecase.dto.CustomerDto;
import com.seuprojeto.integrationtest.domain.CustomerNotFoundException;
import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.database.OrderRepository;
import com.seuprojeto.integrationtest.infra.producer.ProducerCreatedOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
public class CreateOrder {

    private final String customerApiUrl;

    private final OrderRepository repository;

    private final ProducerCreatedOrder producerCreatedOrder;

    public CreateOrder(
            OrderRepository repository,
            ProducerCreatedOrder producerCreatedOrder,
            @Value("${customer.api.url}") String customerApiUrl
    ) {
        this.repository = repository;
        this.producerCreatedOrder = producerCreatedOrder;
        this.customerApiUrl = customerApiUrl;
    }

    @Transactional
    public Order execute(CreateOrderDto createOrderDto) {
        final CustomerDto customer = this.getCustomer(createOrderDto.customerCode());

        final Order order = Order.create(createOrderDto.description(), customer.id(), customer.name(), customer.email());
        this.producerCreatedOrder.send(order);

        return this.repository.save(order);
    }

    private CustomerDto getCustomer(String id) {
        final String url = customerApiUrl + "/{id}";
        try {
            final CustomerDto customer = new RestTemplateBuilder()
                    .build()
                    .getForObject(url, CustomerDto.class, id);

            if (ObjectUtils.isEmpty(customer)) {
                throw new CustomerNotFoundException(id);
            }

            return customer;
        } catch (Exception e) {
            throw new CustomerNotFoundException(id);
        }
    }
}
