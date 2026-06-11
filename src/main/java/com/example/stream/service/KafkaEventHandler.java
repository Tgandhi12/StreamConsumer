package com.example.stream.service;

import com.example.stream.model.CdcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventHandler
        implements EventHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    KafkaEventHandler.class);

    @Override
    public void handle(
            CdcEvent event) {

        log.info(
                "Kafka publish placeholder | txId={}",
                event.transactionId());
    }
}