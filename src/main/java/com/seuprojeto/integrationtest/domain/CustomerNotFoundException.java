package com.seuprojeto.integrationtest.domain;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String code) {
        super("Customer not found: " + code);
    }
}
