package com.example.stream.service;

import com.example.stream.model.CdcEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditEventHandler
        implements EventHandler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AuditEventHandler.class);

    @Override
    public void handle(
            CdcEvent event) {

        log.info(
                "AUDIT | table={} | operation={} | txId={}",
                event.table(),
                event.operationType(),
                event.transactionId());
    }
}