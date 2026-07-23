package org.example.divar.chat.model;

import org.example.divar.model.Advertisement;

import java.util.ArrayList;
import java.util.List;

public class Conversation {

    private Advertisement advertisement;
    private Long adId;
    private String adTitle;
    private String buyerUsername;
    private String sellerUsername;
    private List<Message> messages;
    private Long buyerId;
    private Long sellerId;


    //We create:
    public Conversation(Advertisement advertisement, String buyerUsername, String sellerUsername) {
        this.advertisement = advertisement;
        this.adId = advertisement != null ? advertisement.getId() : null;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.messages = new ArrayList<>();
    }

    //Received from server:
    public Conversation(Long adId, String adTitle, String buyerUsername, String sellerUsername) {
        this.advertisement = null;
        this.adId = adId;
        this.adTitle = adTitle;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.messages = new ArrayList<>();
    }

    public Advertisement getAdvertisement() {
        return advertisement;
    }

    public Long getAdId() {
        return adId;
    }

    public String getAdvertisementTitle() {
        if (advertisement != null) {
            return advertisement.getTitle();
        }
        return adTitle;
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
        return messages.getLast().getText();
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
