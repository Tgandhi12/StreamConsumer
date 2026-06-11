package com.example.stream.websocket;

import com.example.stream.dlq.DeadLetterQueue;
import com.example.stream.metrics.StreamMetrics;
import com.example.stream.parser.CdcEventParser;
import com.example.stream.service.EventProcessor;
import com.example.stream.validation.AsyncApiValidator;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class GoldenGateWebSocketClient extends WebSocketClient {

    private static final Logger log =
            LoggerFactory.getLogger(
                    GoldenGateWebSocketClient.class);

    private final CdcEventParser parser;

    private final EventProcessor processor;

    private final AsyncApiValidator validator =
            new AsyncApiValidator();

    private final DeadLetterQueue dlq =
            new DeadLetterQueue();

    private final StreamMetrics metrics =
            new StreamMetrics();

    public GoldenGateWebSocketClient(
            URI serverUri,
            Map<String, String> headers,
            CdcEventParser parser,
            EventProcessor processor) {

        super(serverUri, headers);

        this.parser = parser;
        this.processor = processor;
    }

    @Override
    public void onOpen(
            ServerHandshake handshake) {

        log.info(
                "Connected to Oracle GoldenGate Data Stream. httpStatus={}",
                handshake.getHttpStatus());
    }

    @Override
    public void onMessage(
            String message) {

        metrics.incrementReceived();

        if (!validator.validate(message)) {

            metrics.incrementValidationFailures();

            dlq.publish(message);

            log.error(
                    "AsyncAPI validation failed. Message moved to DLQ.");

            return;
        }

        parser.parse(message)
                .forEach(event -> {

                    processor.process(event);

                    metrics.incrementProcessed();
                });
    }

    @Override
    public void onClose(
            int code,
            String reason,
            boolean remote) {

        log.warn(
                "GoldenGate WebSocket closed. code={} reason={} remote={}",
                code,
                reason,
                remote);

        log.info(
                "Metrics | received={} processed={} validationFailures={}",
                metrics.getEventsReceived(),
                metrics.getEventsProcessed(),
                metrics.getValidationFailures());
    }

    @Override
    public void onError(
            Exception exception) {

        log.error(
                "GoldenGate WebSocket error",
                exception);
    }
}