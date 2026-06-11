package com.example.stream.dlq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;

public class DeadLetterQueue {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DeadLetterQueue.class);

    private static final String FILE =
            "failed-events.log";

    public void publish(
            String payload) {

        try (FileWriter writer =
                     new FileWriter(
                             FILE,
                             true)) {

            writer.write(payload);
            writer.write(
                    System.lineSeparator());

        } catch (Exception exception) {

            log.error(
                    "Unable to write to DLQ",
                    exception);
        }
    }
}