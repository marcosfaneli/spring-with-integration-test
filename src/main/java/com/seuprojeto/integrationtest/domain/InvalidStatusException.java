package com.seuprojeto.integrationtest.domain;

public class InvalidStatusException extends RuntimeException {
    public InvalidStatusException(String invalidStatus) {
        super("Invalid status: " + invalidStatus);
    }
}
