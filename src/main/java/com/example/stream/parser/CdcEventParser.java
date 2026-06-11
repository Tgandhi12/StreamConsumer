package com.example.stream.parser;

import com.example.stream.model.CdcEvent;
import com.example.stream.model.OperationType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CdcEventParser {
    private static final Logger log = LoggerFactory.getLogger(CdcEventParser.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<CdcEvent> parse(String message) {
        List<CdcEvent> events = new ArrayList<>();

        if (message == null || message.isBlank() || "[]".equals(message.trim())) {
            return events;
        }

        try {
            JsonNode root = objectMapper.readTree(message);
            if (root.isArray()) {
                root.forEach(node -> parseSingle(node).ifPresent(events::add));
            } else {
                parseSingle(root).ifPresent(events::add);
            }
        } catch (Exception exception) {
            log.warn("Skipping invalid JSON message from GoldenGate. message={}", message, exception);
        }

        return events;
    }

    private Optional<CdcEvent> parseSingle(JsonNode node) {
        String operationValue = findText(node, "op_type", "opType", "operation", "operationType");
        Optional<OperationType> operationType = OperationType.from(operationValue);

        if (operationType.isEmpty()) {
            return Optional.empty();
        }

        String table = findText(node, "table", "table_name", "tableName");
        JsonNode before = node.path("before").isMissingNode() ? null : node.path("before");
        JsonNode after = node.path("after").isMissingNode() ? null : node.path("after");

        String txId =
                findText(node,
                        "txid",
                        "transactionId");

        String position =
                findText(node,
                        "position");

        String timestamp =
                findText(node,
                        "timestamp",
                        "commitTimestamp");

        return Optional.of(
                new CdcEvent(
                        table,
                        operationType.get(),
                        txId,
                        position,
                        timestamp,
                        before,
                        after,
                        node));
    }

    private String findText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            JsonNode value = node.get(fieldName);
            if (value != null && !value.isNull()) {
                return value.asText();
            }
        }
        return null;
    }
}
