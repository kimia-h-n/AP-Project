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

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public List<MessageResponse> fetchChat(Long senderId, Long receiverId, Long adId) {
        return chatMapper.toResponseList(chatRepository
                .findChatMessagesBetweenUsersForAd(senderId, receiverId, adId));
    }

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
//                    .lastMessageTime(msg.getSentAt())
                    .senderId(msg.getSender().getId())
                    .adId(ad != null ? ad.getId() : null)
                    .adTitle(ad != null ? ad.getTitle() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    public void saveChat(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }
}
