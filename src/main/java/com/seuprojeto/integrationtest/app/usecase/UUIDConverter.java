package com.seuprojeto.integrationtest.app.usecase;

import java.util.UUID;

public class UUIDConverter {

    private UUIDConverter() {
    }

    static UUID fromIdToUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid id");
        }
    }
}
