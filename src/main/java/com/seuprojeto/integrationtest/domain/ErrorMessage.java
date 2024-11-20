package com.seuprojeto.integrationtest.domain;

import java.time.Instant;

public record ErrorMessage(String message, Instant timestamp) {
}
