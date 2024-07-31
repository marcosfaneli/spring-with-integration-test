package com.seuprojeto.integrationtest.domain;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String id) {
        super("Customer not found with id: " + id);
    }
}
