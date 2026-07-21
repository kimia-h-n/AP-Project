package org.example.divar.service;

import org.example.divar.model.Message;
import org.example.divar.controller.ChatController;

public interface ChatService {
    void connect(Long myUserId, ChatController controller);

    void sendMessage(long otherUserId, Message message);

    void disconnect();
}