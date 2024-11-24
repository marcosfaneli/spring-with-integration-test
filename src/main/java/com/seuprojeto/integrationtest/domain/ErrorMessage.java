package com.seuprojeto.integrationtest.domain;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorMessage(String message, LocalDateTime timestamp, Map<String, String> fields) {
    public ErrorMessage(String message, LocalDateTime timestamp) {
        this(message, timestamp, null);
    }
}
