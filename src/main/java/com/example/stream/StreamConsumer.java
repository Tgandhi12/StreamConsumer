package com.example.stream;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class StreamConsumer {

    public static void main(String[] args) throws Exception {

        String username = "oggadmin";
        String password = "Password";

        String url = "ws://192.168.56.161:7803/services/v2/stream/oggstream?begin=now";

        String authValue = Base64.getEncoder()
                .encodeToString((username + ":" + password)
                        .getBytes(StandardCharsets.UTF_8));

        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Basic " + authValue);

        WebSocketClient client = new WebSocketClient(new URI(url), headers) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to Oracle GoldenGate Data Stream");
            }

            @Override
            public void onMessage(String message) {

                if (message == null || message.trim().equals("[]")) {
                    return;
                }

                if (message.contains("\"op_type\":\"INSERT\"")
                        || message.contains("\"op_type\":\"UPDATE\"")
                        || message.contains("\"op_type\":\"DELETE\"")) {

                    System.out.println("\n========== GoldenGate CDC Event ==========");
                    System.out.println(message);
                    System.out.println("==========================================\n");
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection Closed");
                System.out.println("Code   : " + code);
                System.out.println("Reason : " + reason);
                System.out.println("Remote : " + remote);
            }

            @Override
            public void onError(Exception ex) {
                System.out.println("WebSocket Error:");
                ex.printStackTrace();
            }
        };

        System.out.println("Connecting to GoldenGate stream...");
        client.connectBlocking();

        while (client.isOpen()) {
            Thread.sleep(1000);
        }
    }
}