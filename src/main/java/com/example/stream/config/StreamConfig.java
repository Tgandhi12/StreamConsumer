package com.example.stream.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class StreamConfig {
    private final String streamUrl;
    private final String username;
    private final String password;
    private final boolean reconnectEnabled;
    private final int maxReconnectAttempts;
    private final long initialReconnectDelayMs;
    private final long maxReconnectDelayMs;

    private StreamConfig(Properties properties) {
        this.streamUrl = readValue(properties, "ogg.stream.url", "OGG_STREAM_URL", true);
        this.username = readValue(properties, "ogg.username", "OGG_USER", true);
        this.password = readValue(properties, "ogg.password", "OGG_PASSWORD", true);
        this.reconnectEnabled = Boolean.parseBoolean(readValue(properties, "ogg.reconnect.enabled", "OGG_RECONNECT_ENABLED", false));
        this.maxReconnectAttempts = Integer.parseInt(readValue(properties, "ogg.reconnect.max-attempts", "OGG_RECONNECT_MAX_ATTEMPTS", false));
        this.initialReconnectDelayMs = Long.parseLong(readValue(properties, "ogg.reconnect.initial-delay-ms", "OGG_RECONNECT_INITIAL_DELAY_MS", false));
        this.maxReconnectDelayMs = Long.parseLong(readValue(properties, "ogg.reconnect.max-delay-ms", "OGG_RECONNECT_MAX_DELAY_MS", false));
    }

    public static StreamConfig load() {
        Properties properties = new Properties();
        try (InputStream inputStream = StreamConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load application.properties", exception);
        }
        return new StreamConfig(properties);
    }

    private static String readValue(Properties properties, String propertyKey, String envKey, boolean required) {
        String systemValue = System.getProperty(propertyKey);
        if (hasText(systemValue)) {
            return systemValue.trim();
        }

        String environmentValue = System.getenv(envKey);
        if (hasText(environmentValue)) {
            return environmentValue.trim();
        }

        String fileValue = properties.getProperty(propertyKey);
        if (hasText(fileValue)) {
            return fileValue.trim();
        }

        if (required) {
            throw new IllegalStateException("Missing required config: " + propertyKey + " or environment variable " + envKey);
        }
        return "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public String streamUrl() { return streamUrl; }
    public String username() { return username; }
    public String password() { return password; }
    public boolean reconnectEnabled() { return reconnectEnabled; }
    public int maxReconnectAttempts() { return maxReconnectAttempts; }
    public long initialReconnectDelayMs() { return initialReconnectDelayMs; }
    public long maxReconnectDelayMs() { return maxReconnectDelayMs; }
}
