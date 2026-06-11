package com.example.stream.checkpoint;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckpointStoreTest {

    @Test
    void shouldSaveAndLoadCheckpoint() {

        CheckpointStore store =
                new CheckpointStore();

        store.save(
                "TX999");

        assertEquals(
                "TX999",
                store.load());
    }
}