package org.example.divar.service;

import org.example.divar.model.Advertisement;
import org.example.divar.model.Conversation;
import org.example.divar.model.Message;
import org.example.divar.util.ApiClient;
import org.example.divar.util.AppContext;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConversationServiceHttp implements ConversationService {

    private final ArrayList<Conversation> conversations = new ArrayList<>();

    @Override
    public Conversation findOrCreateConversation(Advertisement ad, String buyerUsername, String sellerUsername) {
        for (Conversation conversation : conversations) {
            if (conversation.getAdvertisement() != null && ad != null &&
                    conversation.getAdvertisement().getId() == ad.getId() &&
                    conversation.getBuyerUsername().equals(buyerUsername)) {
                return conversation;
            }
        }

        Conversation newConversation = new Conversation(ad, buyerUsername, sellerUsername);
        loadIdsAndHistory(newConversation, buyerUsername, sellerUsername);

        conversations.add(newConversation);
        return newConversation;
    }

    @Override
    public ArrayList<Conversation> getConversations() {
        return conversations;
    }

    private void loadIdsAndHistory(Conversation conversation, String buyerUsername, String sellerUsername) {
        try {
            String buyerId = AppContext.getUserService().getUserProfile(buyerUsername).getId();
            String sellerId = AppContext.getUserService().getUserProfile(sellerUsername).getId();

            conversation.setBuyerId(Long.parseLong(buyerId));
            conversation.setSellerId(Long.parseLong(sellerId));

            JSONArray history = ApiClient.getList(
                    "/api/v1/conversations?senderId=" + buyerId + "&receiverId=" + sellerId);

            for (int i = 0; i < history.length(); i++) {
                JSONObject messageJson = history.getJSONObject(i);
                long senderId = messageJson.getLong("senderId");
                String text = messageJson.optString("message", "");

                String senderUsername;
                String receiverUsername;

                if (senderId == conversation.getBuyerId()) {
                    senderUsername = buyerUsername;
                    receiverUsername = sellerUsername;
                } else {
                    senderUsername = sellerUsername;
                    receiverUsername = buyerUsername;
                }

                conversation.addMessage(new Message(senderUsername, receiverUsername, text));
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}