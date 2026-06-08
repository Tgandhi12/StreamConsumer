package com.example.stream.service;

import com.example.stream.model.CdcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventProcessor {
    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    public void process(CdcEvent event) {
        // Enterprise extension point:
        // 1. Validate event schema
        // 2. Publish to Kafka/RabbitMQ
        // 3. Persist audit copy
        // 4. Trigger downstream workflows
        log.info("CDC Event received | table={} | operation={} | before={} | after={}",
                event.table(), event.operationType(), event.before(), event.after());
    }
}
