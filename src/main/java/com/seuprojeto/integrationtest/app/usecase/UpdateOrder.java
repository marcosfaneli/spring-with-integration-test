package com.seuprojeto.integrationtest.app.usecase;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.domain.OrderNotFoundException;
import com.seuprojeto.integrationtest.domain.Status;
import com.seuprojeto.integrationtest.infra.database.OrderRepository;
import com.seuprojeto.integrationtest.app.controller.dto.UpdateOrderDto;
import com.seuprojeto.integrationtest.infra.producer.ProducerUpdatedOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateOrder {

    private final OrderRepository repository;

    private final ProducerUpdatedOrder producerUpdatedOrder;

    public UpdateOrder(OrderRepository repository, ProducerUpdatedOrder producerUpdatedOrder) {
        this.repository = repository;
        this.producerUpdatedOrder = producerUpdatedOrder;
    }

    @Transactional
    public Order execute(UUID id, UpdateOrderDto updateOrderDto) {
        final Order order = this.repository.findById(id).orElseThrow(() -> new OrderNotFoundException(id.toString()));

        final Status status = Status.from(updateOrderDto.status());

        order.update(updateOrderDto.description(), status);

        final Order updatedOrder = this.repository.save(order);

        this.producerUpdatedOrder.send(updatedOrder);

        return updatedOrder;
    }
}
