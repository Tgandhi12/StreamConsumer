package com.example.stream.model;

import com.fasterxml.jackson.databind.JsonNode;

public record CdcEvent(
        String table,
        OperationType operationType,
        JsonNode before,
        JsonNode after,
        JsonNode rawPayload
) {
}
