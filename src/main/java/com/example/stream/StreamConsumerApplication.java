package com.example.stream;

import com.example.stream.config.StreamConfig;
import com.example.stream.parser.CdcEventParser;
import com.example.stream.service.EventProcessor;
import com.example.stream.websocket.GoldenGateStreamRunner;
import java.util.List;

import com.example.stream.service.LoggingEventHandler;
import com.example.stream.service.AuditEventHandler;
import com.example.stream.service.KafkaEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class
StreamConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(StreamConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException {
        StreamConfig config = StreamConfig.load();
        CdcEventParser parser = new CdcEventParser();
        EventProcessor processor =
                new EventProcessor(
                        List.of(
                                new LoggingEventHandler(),
                                new AuditEventHandler(),
                                new KafkaEventHandler()
                        ));

        log.info("Starting Oracle GoldenGate Stream Consumer");
        new GoldenGateStreamRunner(config, parser, processor).start();
    }
}
