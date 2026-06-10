package com.example.stream;

import com.example.stream.config.StreamConfig;
import com.example.stream.parser.CdcEventParser;
import com.example.stream.service.EventProcessor;
import com.example.stream.websocket.GoldenGateStreamRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class
StreamConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(StreamConsumerApplication.class);

    public static void main(String[] args) throws InterruptedException {
        StreamConfig config = StreamConfig.load();
        CdcEventParser parser = new CdcEventParser();
        EventProcessor processor = new EventProcessor();

        log.info("Starting Oracle GoldenGate Stream Consumer");
        new GoldenGateStreamRunner(config, parser, processor).start();
    }
}
