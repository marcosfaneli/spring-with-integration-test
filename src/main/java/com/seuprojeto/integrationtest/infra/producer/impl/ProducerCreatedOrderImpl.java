package com.seuprojeto.integrationtest.infra.producer.impl;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.producer.ProducerCreatedOrder;
import com.seuprojeto.integrationtest.infra.producer.dto.OrderCreatedMessage;
import com.seuprojeto.integrationtest.mapper.OrderMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerCreatedOrderImpl implements ProducerCreatedOrder {

    private static final String TOPIC = "created-order";

    private final KafkaTemplate<String, OrderCreatedMessage> kafkaTemplate;
    private final OrderMapper mapper;

    public ProducerCreatedOrderImpl(KafkaTemplate<String, OrderCreatedMessage> kafkaTemplate, OrderMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void send(Order order) {
        this.kafkaTemplate.send(TOPIC, this.mapper.toOrderCreatedMessage(order));
    }
}
