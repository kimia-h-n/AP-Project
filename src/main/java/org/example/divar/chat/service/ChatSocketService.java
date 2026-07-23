package org.example.divar.chat.service;
import org.example.divar.chat.model.Message;
import org.example.divar.util.ApiConfig;
import org.example.divar.util.SessionManager;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletionStage;

import javafx.application.Platform;

public class ChatSocketService implements WebSocket.Listener {

    private final Long myUserId;
    private final ChatService.MessageListener listener;

    private WebSocket webSocket;
    private volatile boolean stompConnected = false;
    private final StringBuilder incomingBuffer = new StringBuilder();

    public ChatSocketService(Long myUserId, ChatService.MessageListener listener) {
        this.myUserId = myUserId;
        this.listener = listener;
    }

    public void connect() {
        String wsUrl = ApiConfig.BASE_URL.replaceFirst("^http", "ws") + "/chat-native";

        HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), this)
                .thenAccept(ws -> this.webSocket = ws)
                .exceptionally(error -> {
                    if (listener != null) {
                        listener.onError(error);
                    }
                    return null;
                });
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        this.webSocket = webSocket;

        String token = SessionManager.getToken();
        if (token == null || token.isBlank()) {
            if (listener != null) {
                listener.onError(new IllegalStateException("JWT token is missing"));
            }
            webSocket.abort();
            return;
        }
        String frame = "CONNECT\n"
                + "accept-version:1.2\n"
                + "host:localhost\n"
                + "Authorization:Bearer " + token + "\n\n\0";

        webSocket.sendText(frame, true);
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        incomingBuffer.append(data);
        if (last) {
            String raw = incomingBuffer.toString();
            incomingBuffer.setLength(0);
            handleFrame(raw);
        }
        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        incomingBuffer.append(new String(bytes, StandardCharsets.UTF_8));
        if (last) {
            String raw = incomingBuffer.toString();
            incomingBuffer.setLength(0);
            handleFrame(raw);
        }
        return WebSocket.Listener.super.onBinary(webSocket, data, last);
    }

    private void handleFrame(String frame) {
        if (frame == null || frame.isBlank()) return;

        if (frame.startsWith("CONNECTED")) {
            stompConnected = true;
            if (listener != null) {
                Platform.runLater(() -> listener.onConnected());
            }

            sendRaw(
                    "SUBSCRIBE\n"
                            + "id:sub-0\n"
                            + "destination:/queue/messages-" + myUserId + "\n\n\0"
            );
            return;
        }

        if (frame.startsWith("MESSAGE")) {
            String body = extractBody(frame);
            if (body == null || body.isBlank()) return;

            try {
                JSONObject json = new JSONObject(body);

                Long adId = json.has("adId") && !json.isNull("adId")
                        ? json.getLong("adId")
                        : null;

                Message msg = new Message(
                        json.optLong("id", 0L),
                        json.getLong("senderId"),
                        json.getLong("receiverId"),
                        json.getString("message"),
                        Instant.parse(json.getString("sentAt")),
                        adId
                );
                if (listener != null) {
                    Platform.runLater(() -> listener.onMessage(msg));
                }
            } catch (Exception e) {
                if (listener != null) {
                    Platform.runLater(() -> listener.onError(e));
                }
            }
            return;
        }

        if (frame.startsWith("ERROR")) {
            if (listener != null) {
                Platform.runLater(() -> listener.onError(new RuntimeException("STOMP Error: " + frame)));
            }
        }
    }

    private String extractBody(String frame) {
        int idx = frame.indexOf("\n\n");
        if (idx < 0) return null;
        String body = frame.substring(idx + 2);
        return body.replace("\0", "").trim();
    }

    public void sendLiveMessage(Message msg) {
        if (!stompConnected) {
            throw new IllegalStateException("STOMP is not connected yet");
        }

        JSONObject body = new JSONObject();
        body.put("receiverId", msg.getReceiverId());
        body.put("message", msg.getText());

        body.put("adId", msg.getAdId());

        sendRaw(
                "SEND\n"
                        + "destination:/app/sendMessage\n"
                        + "content-type:application/json\n"
                        + "content-length:" + body.toString().getBytes(StandardCharsets.UTF_8).length + "\n\n"
                        + body
                        + "\0"
        );
    }

    private void sendRaw(String frame) {
        if (webSocket == null) {
            throw new IllegalStateException("WebSocket is not connected");
        }
        webSocket.sendText(frame, true);
    }

    public void close() {
        try {
            if (webSocket != null) {
                if (stompConnected) {
                    webSocket.sendText("DISCONNECT\n\n\0", true);
                }
                webSocket.abort();
            }
        } finally {
            stompConnected = false;
            webSocket = null;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        stompConnected = false;
        if (listener != null) {
            listener.onError(error);
        }
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        stompConnected = false;
        if (listener != null) {
            listener.onError(new RuntimeException("WebSocket closed: " + statusCode + " - " + reason));
        }
        return WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
    }
}
