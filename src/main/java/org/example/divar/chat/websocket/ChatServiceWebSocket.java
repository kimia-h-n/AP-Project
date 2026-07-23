package org.example.divar.chat.websocket;
import org.example.divar.chat.model.Message;
import org.example.divar.chat.service.ChatService;
import org.example.divar.chat.service.ChatSocketService;

import java.util.Objects;

public class ChatServiceWebSocket implements ChatService {

    private ChatSocketService socketService;

    @Override
    public synchronized void connect(Long myUserId, MessageListener listener) {
        Objects.requireNonNull(myUserId, "myUserId cannot be null");
        Objects.requireNonNull(listener, "listener cannot be null");

        disconnect();

        socketService = new ChatSocketService(myUserId, listener);
        socketService.connect();
    }

    @Override
    public synchronized void sendMessage(Message message) {
        System.out.println("Trying to send message");
        Objects.requireNonNull(message, "message cannot be null");

        if (socketService == null) {
            throw new IllegalStateException("Chat service is not connected");
        }

        socketService.sendLiveMessage(message);
    }

    @Override
    public synchronized void disconnect() {
        if (socketService == null) return;
        socketService.close();
        socketService = null;
    }
}
