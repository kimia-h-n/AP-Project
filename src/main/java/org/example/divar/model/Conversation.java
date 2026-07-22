package org.example.divar.model;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private Advertisement advertisement;
    private String buyerUsername;
    private String sellerUsername;
    private List<Message> messages;
    private Long buyerId;
    private Long sellerId;

    public Conversation(Advertisement advertisement, String buyerUsername, String sellerUsername) {
        this.advertisement = advertisement;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.messages = new ArrayList<>();
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public String getAdvertisementTitle() {
        if (advertisement != null) {
            return advertisement.getTitle();
        }
        return "No Title";
    }

    public String getBuyerUsername() {
        return buyerUsername;
    }


    public String getSellerUsername() {
        return sellerUsername;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }


    public String getLastMessage() {
        if (messages.isEmpty()) {
            return "No messages";
        }
        return messages.get(messages.size() - 1).getText();
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }
}

