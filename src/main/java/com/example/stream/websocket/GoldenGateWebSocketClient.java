package com.example.stream.websocket;

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
    private static final Logger log = LoggerFactory.getLogger(GoldenGateWebSocketClient.class);

    private final CdcEventParser parser;
    private final EventProcessor processor;
    private final AsyncApiValidator validator =
            new AsyncApiValidator();

    public GoldenGateWebSocketClient(URI serverUri,
                                     Map<String, String> headers,
                                     CdcEventParser parser,
                                     EventProcessor processor) {
        super(serverUri, headers);
        this.parser = parser;
        this.processor = processor;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        log.info("Connected to Oracle GoldenGate Data Stream. httpStatus={}", handshake.getHttpStatus());
    }

    @Override
    public void onMessage(String message) {

        if (!validator.validate(message)) {

            log.error(
                    "AsyncAPI validation failed. Message rejected.");

            return;
        }

        parser.parse(message)
                .forEach(processor::process);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("GoldenGate WebSocket closed. code={} reason={} remote={}", code, reason, remote);
    }

    @Override
    public void onError(Exception exception) {
        log.error("GoldenGate WebSocket error", exception);
    }
}
