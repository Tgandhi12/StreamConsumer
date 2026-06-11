package com.example.stream.service;

import com.example.stream.model.CdcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingEventHandler implements EventHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    LoggingEventHandler.class);

    @Override
    public void handle(CdcEvent event) {

        log.info(
                "CDC Event received | table={} | operation={} | txId={} | position={}",
                event.table(),
                event.operationType(),
                event.transactionId(),
                event.position());
    }
}