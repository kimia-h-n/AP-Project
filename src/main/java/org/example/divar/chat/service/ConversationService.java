package org.example.divar.chat.service;

import org.example.divar.model.Advertisement;
import org.example.divar.chat.model.Conversation;
import java.util.ArrayList;

/**
 * Interface for managing chat conversations.
 */
public interface ConversationService {

    /**
     * Finds an existing conversation or creates a new one.
     */
    Conversation findOrCreateConversation(Advertisement ad, String buyerUsername, String sellerUsername);

    /**
     * Retrieves all conversations for the current user.
     */
    ArrayList<Conversation> getConversations();
}
