package org.example.divar.service;

import javafx.application.Platform;
import org.example.divar.controller.ChatController;
import org.example.divar.util.ApiConfig;
import org.example.divar.util.SessionManager;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class ChatSocketService extends WebSocketClient {

    private final Long myUserId;
    private final ChatController controller;

    public ChatSocketService(Long myUserId, ChatController controller) {
        super(URI.create(ApiConfig.BASE_URL
                .replaceFirst("^https", "wss")
                .replaceFirst("^http", "ws")
                + "/chat/websocket"), getHeaders());

        this.myUserId = myUserId;
        this.controller = controller;
    }

    private static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + SessionManager.getToken());
        return headers;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("اتصال چت برقرار شد.");
        String subscribeFrame = "SUBSCRIBE\ndestination:/queue/messages-" + myUserId + "\n\n\u0000";
        send(subscribeFrame);
    }

    @Override
    public void onMessage(String frame) {
        if (!frame.contains("MESSAGE")) return;

        try {
            int jsonStart = frame.indexOf("{");
            int jsonEnd = frame.lastIndexOf("}") + 1;
            String jsonBody = frame.substring(jsonStart, jsonEnd);

            JSONObject json = new JSONObject(jsonBody);
            long senderId = json.getLong("senderId");
            long receiverId = json.getLong("receiverId");
            String text = json.optString("message", "");

            if (controller != null) {
                Platform.runLater(() -> controller.onLiveMessageReceived(senderId, receiverId, text));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLiveMessage(Long receiverId, String text) {
        if (this.isOpen()) {
            JSONObject body = new JSONObject();
            body.put("receiverId", receiverId);
            body.put("message", text);

            String sendFrame = "SEND\ndestination:/app/sendMessage\ncontent-type:application/json\n\n" + body + "\u0000";
            send(sendFrame);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("اتصال چت قطع شد: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}