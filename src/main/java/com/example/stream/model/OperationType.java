package com.example.stream.model;

import java.util.Optional;

public enum OperationType {
    INSERT,
    UPDATE,
    DELETE;

    public static Optional<OperationType> from(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(OperationType.valueOf(value.trim().toUpperCase()));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
