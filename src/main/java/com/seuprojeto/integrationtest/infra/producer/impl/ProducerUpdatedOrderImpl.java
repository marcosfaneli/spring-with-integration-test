package com.seuprojeto.integrationtest.infra.producer.impl;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.producer.ProducerUpdatedOrder;
import com.seuprojeto.integrationtest.infra.producer.dto.OrderUpdatedMessage;
import com.seuprojeto.integrationtest.mapper.OrderMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProducerUpdatedOrderImpl implements ProducerUpdatedOrder {

    private static final String TOPIC = "updated-order";

    private final KafkaTemplate<String, OrderUpdatedMessage> kafkaTemplate;

    private final OrderMapper mapper;

    public ProducerUpdatedOrderImpl(KafkaTemplate<String, OrderUpdatedMessage> kafkaTemplate, OrderMapper mapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.mapper = mapper;
    }

    @Override
    public void send(Order order) {
        this.kafkaTemplate.send(TOPIC, this.mapper.toOrderUpdatedMessage(order));
    }
}
