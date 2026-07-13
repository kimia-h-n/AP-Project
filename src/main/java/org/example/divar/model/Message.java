package org.example.divar.model;

import java.time.LocalDateTime;

public class Message {
    private String senderUsername;
    private String receiverUsername;
    private String text;
    private LocalDateTime time;

    public Message(String senderUsername, String receiverUsername, String text) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.text = text;
        this.time = LocalDateTime.now();
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
