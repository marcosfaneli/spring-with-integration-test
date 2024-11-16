package com.seuprojeto.integrationtest.infra.producer;

import com.seuprojeto.integrationtest.domain.Order;

public interface ProducerCreatedOrder {
    void send(Order order);
}
