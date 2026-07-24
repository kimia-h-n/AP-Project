package org.example.divar.chat.model;

import org.example.divar.model.Advertisement;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a chat conversation between a buyer and a seller about an advertisement.
 * It has two constructors: one when we create it locally, and another when we receive it from the server.
 * It also keeps track of messages, IDs, usernames, and can return the last message text.
 */
public class Conversation {

    private Advertisement advertisement;
    private Long adId;
    private String adTitle;
    private String buyerUsername;
    private String sellerUsername;
    private List<Message> messages;
    private Long buyerId;
    private Long sellerId;


    /**
     * Constructor used when we create a new conversation locally.
     *
     * @param advertisement the ad object related to this chat
     * @param buyerUsername the username of the buyer
     * @param sellerUsername the username of the seller
     */
    public Conversation(Advertisement advertisement, String buyerUsername, String sellerUsername) {
        this.advertisement = advertisement;
        this.adId = advertisement != null ? advertisement.getId() : null;
        this.buyerUsername = buyerUsername;
        this.sellerUsername = sellerUsername;
        this.messages = new ArrayList<>();
    }

    /**
     * Constructor used when we receive conversation data from the server.
     *
     * @param adId the ID of the advertisement
     * @param adTitle the title of the advertisement
     * @param buyerUsername the username of the buyer
     * @param sellerUsername the username of the seller
     */
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

    /**
     * Returns the advertisement title, checking if we have the ad object or just the title string.
     */
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

    /**
     * Returns the text of the last message in the conversation, or a default text if there are no messages.
     */
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
