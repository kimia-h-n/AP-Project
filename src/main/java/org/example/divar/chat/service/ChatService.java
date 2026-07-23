package org.example.divar.chat.service;

import org.example.divar.chat.model.Message;

public interface ChatService {
    //Sending message:
    void connect(Long myUserId, MessageListener listener);
    void sendMessage(Message message);
    void disconnect();
    //Receiving message
    interface MessageListener {
        void onMessage(Message message);
        default void onConnected() {}
        default void onError(Throwable error) {}
    }
}