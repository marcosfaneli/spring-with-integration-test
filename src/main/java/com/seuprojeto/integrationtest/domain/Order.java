package com.seuprojeto.integrationtest.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "public")
public class Order {

    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Status status;
    private String customerCode;
    private String customerName;
    private String customerEmail;

    public Order() {
    }

    public Order(
            UUID id,
            String description,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Status status,
            String customerCode,
            String customerName,
            String customerEmail) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.customerCode = customerCode;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public static Order create(
            String description,
            String customerCode,
            String customerName,
            String customerEmail) {
        return new Order(
                UUID.randomUUID(),
                description,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Status.OPENED,
                customerCode,
                customerName,
                customerEmail);
    }

    public UUID getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Status getStatus() {
        return status;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void update(String description, String status) {
        this.description = description;
        this.status = Status.valueOf(status.toUpperCase());
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                ", customerCode='" + customerCode + '\'' +
                ", customerName='" + customerName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}
