package com.seuprojeto.integrationtest.infra.producer;

import com.seuprojeto.integrationtest.domain.Order;

public interface ProducerUpdatedOrder {
    void send(Order order);
}
