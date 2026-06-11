package com.example.stream.parser;

import com.example.stream.model.CdcEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CdcEventParserTest {

    private final CdcEventParser parser =
            new CdcEventParser();

    @Test
    void shouldParseInsertEvent() {

        String payload = """
                {
                  "table":"EMPLOYEE",
                  "op_type":"INSERT",
                  "txid":"TX123"
                }
                """;

        List<CdcEvent> events =
                parser.parse(payload);

        assertEquals(
                1,
                events.size());

        assertEquals(
                "EMPLOYEE",
                events.get(0).table());

        assertEquals(
                "TX123",
                events.get(0).transactionId());
    }

    @Test
    void shouldIgnoreInvalidOperation() {

        String payload = """
                {
                  "table":"EMPLOYEE",
                  "op_type":"UPSERT"
                }
                """;

        List<CdcEvent> events =
                parser.parse(payload);

        assertTrue(
                events.isEmpty());
    }
}