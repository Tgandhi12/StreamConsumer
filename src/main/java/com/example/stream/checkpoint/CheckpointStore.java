package com.example.stream.checkpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class CheckpointStore {

    private static final Logger log =
            LoggerFactory.getLogger(
                    CheckpointStore.class);

    private static final String FILE =
            "checkpoint.dat";

    public void save(String txId) {

        if (txId == null || txId.isBlank()) {
            return;
        }

        try {

            Files.writeString(
                    Path.of(FILE),
                    txId);

            log.info(
                    "Checkpoint saved: {}",
                    txId);

        } catch (Exception exception) {

            log.error(
                    "Unable to save checkpoint",
                    exception);
        }
    }
    public boolean exists() {

        return java.nio.file.Files.exists(
                java.nio.file.Path.of(FILE));
    }

    public String load() {

        try {

            Path path =
                    Path.of(FILE);

            if (!Files.exists(path)) {
                return null;
            }

            return Files.readString(path);

        } catch (Exception exception) {

            log.error(
                    "Unable to load checkpoint",
                    exception);

            return null;
        }
    }
}