package com.seuprojeto.integrationtest.infra.producer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seuprojeto.integrationtest.domain.Status;

import java.time.LocalDateTime;

public class OrderUpdatedMessage {

        private String id;
        private String customerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String status;

        public OrderUpdatedMessage() {
        }

        public OrderUpdatedMessage(String id, String customerId, LocalDateTime createdAt, LocalDateTime updatedAt, Status status) {
            this.id = id;
            this.customerId = customerId;
            this.createdAt = createdAt;
            this.status = status.name();
            this.updatedAt = updatedAt;
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

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        public LocalDateTime getUpdatedAt() {
            return this.updatedAt;
        }

        public String getStatus() {
            return status;
        }
}
