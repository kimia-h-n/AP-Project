package com.example.sales.chat.service;


import com.example.sales.chat.ChatMapper;
import com.example.sales.chat.model.ChatMessage;
import com.example.sales.chat.ChatRepository;
import com.example.sales.chat.dto.ConversationSummary;
import com.example.sales.chat.dto.MessageResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service containing chat conversation business logic.
 */
@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    /**
     * Fetches all messages between two users for a specific ad.
     *
     * @param senderId   sender user id
     * @param receiverId receiver user id
     * @param adId       ad id
     * @return list of message responses
     */
    public List<MessageResponse> fetchChat(Long senderId, Long receiverId, Long adId) {
        return chatMapper.toResponseList(chatRepository
                .findChatMessagesBetweenUsersForAd(senderId, receiverId, adId));
    }

    /**
     * Returns a summary of all active conversations for a user.
     *
     * @param userId user id
     * @return list of conversation summaries
     */
    public List<ConversationSummary> getUserConversations(Long userId) {
        List<ChatMessage> latestMessages = chatRepository.findActiveConversationsForUser(userId);

        return latestMessages.stream().map(msg -> {
            var contact = msg.getSender().getId().equals(userId) ? msg.getReceiver() : msg.getSender();
            var ad = msg.getAd();

            return ConversationSummary.builder()
                    .contactId(contact.getId())
                    .contactName(contact.getFirstname() + contact.getLastname())
                    .contactUsername(contact.getUsername())
                    .lastMessage(msg.getMessage())
                    .senderId(msg.getSender().getId())
                    .adId(ad != null ? ad.getId() : null)
                    .adTitle(ad != null ? ad.getTitle() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * Persists a chat message.
     *
     * @param chatMessage chat message entity
     */
    public void saveChat(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }
}
