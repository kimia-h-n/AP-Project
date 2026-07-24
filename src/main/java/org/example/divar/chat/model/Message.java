package org.example.divar.chat.model;

import java.time.Instant;

/**
 * This class represents a chat message sent between users regarding an advertisement.
 * It stores sender and receiver IDs, message text, status (like PENDING or RECEIVED),
 * timestamp when it was sent, and the related ad ID.
 */
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

    /**
     * Constructor used when creating a new message locally before sending it to the server.
     * Sets the status to PENDING and the timestamp to the current time.
     *
     * @param senderId the ID of the user sending the message
     * @param receiverId the ID of the user receiving the message
     * @param text the message content
     * @param adId the ID of the related advertisement
     */
    public Message(Long senderId, Long receiverId, String text, Long adId) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.adId = adId;
        this.status = "PENDING";
        this.sentAt = Instant.now();
    }

    /**
     * Constructor used when loading an existing message received from the server.
     *
     * @param id the unique message ID
     * @param senderId the ID of the sender
     * @param receiverId the ID of the receiver
     * @param text the message content
     * @param sentAt the timestamp when the message was originally sent
     * @param adId the ID of the related advertisement
     */
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