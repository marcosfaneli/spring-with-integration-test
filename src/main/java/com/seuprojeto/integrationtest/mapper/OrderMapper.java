package com.seuprojeto.integrationtest.mapper;

import com.seuprojeto.integrationtest.domain.Order;
import com.seuprojeto.integrationtest.infra.producer.dto.OrderCreatedMessage;
import org.springframework.stereotype.Service;

@Service
public class OrderMapper {

    public OrderCreatedMessage toOrderCreatedMessage(Order order) {
        return  new OrderCreatedMessage(order.getId().toString(), order.getCustomerCode(), order.getCreatedAt(), order.getStatus());
    }
}
