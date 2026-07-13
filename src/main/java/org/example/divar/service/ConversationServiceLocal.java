package org.example.divar.service;

import org.example.divar.model.Advertisement;
import org.example.divar.model.Conversation;
import java.util.ArrayList;

public class ConversationServiceLocal implements ConversationService {

    private final ArrayList<Conversation> conversations = new ArrayList<>();

    @Override
    public Conversation findOrCreateConversation(Advertisement ad, String buyerUsername, String sellerUsername) {
        for (Conversation conversation : conversations) {
            boolean sameAd = conversation.getAdvertisement().getId() == ad.getId();
            boolean sameBuyer = conversation.getBuyerUsername().equals(buyerUsername);

            if (sameAd && sameBuyer) {
                return conversation;
            }
        }
        Conversation newConversation = new Conversation(ad, buyerUsername, sellerUsername);
        conversations.add(newConversation);
        return newConversation;
    }

    @Override
    public ArrayList<Conversation> getConversations() {
        return conversations;
    }
}

