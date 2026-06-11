package com.example.stream.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AsyncApiValidatorTest {

    private final AsyncApiValidator validator =
            new AsyncApiValidator();

    @Test
    void shouldAcceptValidEvent() {

        String event = """
                {
                  "table":"TEST.T1",
                  "op_type":"INSERT"
                }
                """;

        assertTrue(
                validator.validate(event));
    }
    @Test
    void shouldRejectInvalidJson() {

        String event = """
            {
              "table":"EMPLOYEE",
            """;

        assertFalse(
                validator.validate(event));
    }
    @Test
    void shouldAcceptEventWithoutTable() {

        String event = """
            {
              "op_type":"INSERT"
            }
            """;

        assertTrue(
                validator.validate(event));
    }

    @Test
    void shouldRejectMissingOpType() {

        String event = """
                {
                  "table":"TEST.T1"
                }
                """;

        assertFalse(
                validator.validate(event));
    }

    @Test
    void shouldRejectInvalidOperation() {

        String event = """
                {
                  "table":"TEST.T1",
                  "op_type":"UPSERT"
                }
                """;

        assertFalse(
                validator.validate(event));
    }
}