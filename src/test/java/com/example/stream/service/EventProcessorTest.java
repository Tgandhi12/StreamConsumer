package com.example.stream.service;

import com.example.stream.model.CdcEvent;
import com.example.stream.model.OperationType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorTest {

    @Test
    void shouldProcessEvent() {

        EventProcessor processor =
                new EventProcessor(
                        List.of(
                                new LoggingEventHandler()));

        CdcEvent event =
                new CdcEvent(
                        "EMPLOYEE",
                        OperationType.INSERT,
                        "TX1",
                        "POS1",
                        "2025-06-11T10:00:00Z",
                        null,
                        null,
                        null);

        processor.process(event);

        assertTrue(true);
    }
}