package com.seuprojeto.integrationtest.domain;

public enum Status {
    OPENED, CLOSED, CANCELED;

    public static Status from(String status) {
        return switch (status.trim().toUpperCase()) {
            case "OPENED" -> OPENED;
            case "CLOSED" -> CLOSED;
            case "CANCELED" -> CANCELED;
            default -> throw new InvalidStatusException(status);
        };
    }
}
