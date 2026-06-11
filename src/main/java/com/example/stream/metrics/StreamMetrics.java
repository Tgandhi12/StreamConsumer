package com.example.stream.metrics;

import java.util.concurrent.atomic.AtomicLong;

public class StreamMetrics {

    private final AtomicLong eventsReceived =
            new AtomicLong();

    private final AtomicLong eventsProcessed =
            new AtomicLong();

    private final AtomicLong validationFailures =
            new AtomicLong();

    public void incrementReceived() {
        eventsReceived.incrementAndGet();
    }

    public void incrementProcessed() {
        eventsProcessed.incrementAndGet();
    }

    public void incrementValidationFailures() {
        validationFailures.incrementAndGet();
    }

    public long getEventsReceived() {
        return eventsReceived.get();
    }

    public long getEventsProcessed() {
        return eventsProcessed.get();
    }

    public long getValidationFailures() {
        return validationFailures.get();
    }
}