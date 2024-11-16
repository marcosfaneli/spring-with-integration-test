package com.seuprojeto.integrationtest.infra.producer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seuprojeto.integrationtest.domain.Status;

import java.time.LocalDateTime;

public class OrderCreatedMessage {

        private String id;
        private String customerId;
        private LocalDateTime createdAt;
        private String status;

        public OrderCreatedMessage() {
        }

        public OrderCreatedMessage(String id, String customerId, LocalDateTime createdAt, Status status) {
            this.id = id;
            this.customerId = customerId;
            this.createdAt = createdAt;
            this.status = status.name();
        }

        public String getId() {
            return id;
        }

        public String getCustomerId() {
            return customerId;
        }

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public String getStatus() {
            return status;
        }
}
