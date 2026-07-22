package org.example.divar.service;

import org.example.divar.model.Message;
import org.example.divar.controller.ChatController;

public class ChatServiceWebSocket implements ChatService {

    private ChatSocketService socketService;

    public void connect(Long myUserId, ChatController controller) {
        this.socketService = new ChatSocketService(myUserId, controller);
        this.socketService.connect();
    }

    @Override
    public void sendMessage(long otherUserId, Message message) {
        if (socketService != null) {
            socketService.sendLiveMessage(otherUserId, message.getText());
        }
    }

    @Override
    public void disconnect() {
        if (socketService != null) {
            socketService.close();
        }
    }
}
