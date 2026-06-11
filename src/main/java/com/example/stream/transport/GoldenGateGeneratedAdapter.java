package com.example.stream.transport;

import com.example.stream.parser.CdcEventParser;
import com.example.stream.service.EventProcessor;
import com.example.stream.validation.AsyncApiValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoldenGateGeneratedAdapter {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GoldenGateGeneratedAdapter.class);

    private final AsyncApiValidator validator;

    private final CdcEventParser parser;

    private final EventProcessor processor;

    public GoldenGateGeneratedAdapter(
            AsyncApiValidator validator,
            CdcEventParser parser,
            EventProcessor processor) {

        this.validator = validator;
        this.parser = parser;
        this.processor = processor;
    }

    public void consume(
            String payload) {

        if (!validator.validate(payload)) {

            log.warn(
                    "Generated client payload rejected");

            return;
        }

        parser.parse(payload)
                .forEach(processor::process);
    }
}