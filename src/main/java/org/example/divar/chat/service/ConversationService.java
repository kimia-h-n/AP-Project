package org.example.divar.chat.service;

import org.example.divar.model.Advertisement;
import org.example.divar.chat.model.Conversation;
import java.util.ArrayList;

public interface ConversationService {

    Conversation findOrCreateConversation(Advertisement ad, String buyerUsername, String sellerUsername);

    ArrayList<Conversation> getConversations();
}

