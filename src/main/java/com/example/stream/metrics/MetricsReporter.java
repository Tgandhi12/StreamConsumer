package com.example.stream.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsReporter {

    private static final Logger log =
            LoggerFactory.getLogger(
                    MetricsReporter.class);

    public void report(
            StreamMetrics metrics) {

        log.info(
                "Metrics Snapshot | received={} processed={} failures={}",
                metrics.getEventsReceived(),
                metrics.getEventsProcessed(),
                metrics.getValidationFailures());
    }
}