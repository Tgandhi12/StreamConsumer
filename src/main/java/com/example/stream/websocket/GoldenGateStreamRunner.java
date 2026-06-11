package com.example.stream.websocket;

import com.example.stream.config.StreamConfig;
import com.example.stream.parser.CdcEventParser;
import com.example.stream.service.EventProcessor;
import com.example.stream.checkpoint.CheckpointStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class GoldenGateStreamRunner {
    private static final Logger log = LoggerFactory.getLogger(GoldenGateStreamRunner.class);

    private final StreamConfig config;
    private final CdcEventParser parser;
    private final CheckpointStore checkpointStore =
            new CheckpointStore();
    private final EventProcessor processor;

    public GoldenGateStreamRunner(StreamConfig config, CdcEventParser parser, EventProcessor processor) {
        this.config = config;
        this.parser = parser;
        this.processor = processor;
    }

    public void start() throws InterruptedException {
        String lastCheckpoint =
                checkpointStore.load();

        if (lastCheckpoint != null) {

            log.info(
                    "Recovered checkpoint: {}",
                    lastCheckpoint);
        }
        int attempt = 0;
        long delayMs = config.initialReconnectDelayMs();

        while (!Thread.currentThread().isInterrupted()) {
            attempt++;
            try {
                log.info("Connecting to GoldenGate stream. url={} attempt={}", config.streamUrl(), attempt);
                GoldenGateWebSocketClient client = createClient();
                client.connectBlocking();

                while (client.isOpen()) {
                    Thread.sleep(1000);
                }

                if (!config.reconnectEnabled()) {
                    log.info("Reconnect disabled. Exiting stream runner.");
                    return;
                }

                if (attempt >= config.maxReconnectAttempts()) {
                    log.error("Maximum reconnect attempts reached. attempts={}", config.maxReconnectAttempts());
                    return;
                }

                log.warn("Disconnected. Reconnecting after {} ms", delayMs);
                Thread.sleep(delayMs);
                delayMs = Math.min(delayMs * 2, config.maxReconnectDelayMs());
            } catch (Exception exception) {
                log.error("Failed while connecting or consuming GoldenGate stream", exception);

                if (!config.reconnectEnabled() || attempt >= config.maxReconnectAttempts()) {
                    return;
                }

                log.warn("Retrying after {} ms", delayMs);
                Thread.sleep(delayMs);
                delayMs = Math.min(delayMs * 2, config.maxReconnectDelayMs());
            }
        }
    }

    private GoldenGateWebSocketClient createClient() throws Exception {
        return new GoldenGateWebSocketClient(new URI(config.streamUrl()), authHeaders(), parser, processor);
    }

    private Map<String, String> authHeaders() {
        String basicToken = Base64.getEncoder()
                .encodeToString((config.username() + ":" + config.password()).getBytes(StandardCharsets.UTF_8));
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + basicToken);
        return headers;
    }
}
