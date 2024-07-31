package com.seuprojeto.integrationtest.domain;

public enum Status {
    OPENED, CLOSED, CANCELED;

    public static Status fromString(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Invalid status: null");
        }

        return switch (status.toUpperCase()) {
            case "OPENED" -> OPENED;
            case "CLOSED" -> CLOSED;
            case "CANCELED" -> CANCELED;
            default -> throw new IllegalArgumentException("Invalid status: " + status);
        };
    }
}
