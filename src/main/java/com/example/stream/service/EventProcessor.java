package com.example.stream.service;

import com.example.stream.checkpoint.CheckpointStore;
import com.example.stream.model.CdcEvent;

import java.util.List;

public class EventProcessor {

    private final List<EventHandler> handlers;

    private final CheckpointStore checkpointStore =
            new CheckpointStore();

    public EventProcessor(
            List<EventHandler> handlers) {

        this.handlers = handlers;
    }

    public void process(
            CdcEvent event) {

        handlers.forEach(
                handler ->
                        handler.handle(event));

        checkpointStore.save(
                event.transactionId());
    }
}