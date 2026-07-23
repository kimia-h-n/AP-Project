package org.example.divar.chat.model;

import java.time.Instant;

public class Message {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String text;
    private String status; // PENDING, SENT, RECEIVED
    private Instant sentAt;
    private Long adId;

    public Instant getSentAt() {
        return sentAt;
    }

    public Message(Long senderId, Long receiverId, String text, Long adId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.adId = adId;
        this.status = "PENDING";
        this.sentAt = Instant.now();
    }

    public Message(Long id, Long senderId, Long receiverId, String text, Instant sentAt, Long adId) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.adId = adId;
        this.status = "RECEIVED";
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getText() {
        return text;
    }

    public Long getAdId() {
        return adId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
