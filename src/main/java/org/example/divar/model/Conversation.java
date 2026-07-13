package org.example.divar.model;

import java.util.ArrayList;
import java.util.List;

public class Conversation {
    private Advertisement advertisement;
    private String buyerUsername;
    private String sellerUsername;
    private List<Message> messages;

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
        return advertisement != null ? advertisement.getTitle() : "بدون عنوان";
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
            return "پیامی وجود ندارد";
        }
        return messages.get(messages.size() - 1).getText();
    }
}

