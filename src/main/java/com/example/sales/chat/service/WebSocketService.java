package com.example.sales.chat.service;


import com.example.sales.ad.Ad;
import com.example.sales.ad.AdRepository;
import com.example.sales.ad.dto.AdResponse;
import com.example.sales.chat.ChatMapper;
import com.example.sales.chat.ChatRepository;
import com.example.sales.chat.dto.UpdateSeenRequest;
import com.example.sales.chat.model.ChatMessage;
import com.example.sales.chat.dto.MessageRequest;
import com.example.sales.chat.model.MessageStatus;
import com.example.sales.exception.AdNotFoundException;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class WebSocketService {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final AdRepository adRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMapper chatMapper;
    private final ChatRepository chatRepository;

    public void sendMessage(MessageRequest request, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(request.getReceiverId()).orElseThrow(UserNotFoundException::new);
        Ad ad = adRepository.findById(request.getAdId()).orElseThrow(AdNotFoundException::new);
        ChatMessage chatMessage = ChatMessage.builder()
                .sentAt(Instant.now())
                .status(MessageStatus.SENT)
                .message(request.getMessage())
                .ad(ad)
                .sender(sender)
                .receiver(receiver)
                .build();

        chatService.saveChat(chatMessage);
        String receiverDestination = "/queue/messages-" + chatMessage.getReceiver().getId();
        simpMessagingTemplate.convertAndSend(receiverDestination, chatMapper.toResponse(chatMessage));
    }

    @Transactional
    public void markConversationSeen(Long senderId, Principal principal) {
        User receiver = userRepository.findByUsername(principal.getName())
                .orElseThrow(UserNotFoundException::new);

        List<ChatMessage> unreadMessages = chatRepository
                .findBySenderIdAndReceiverIdAndStatus(senderId, receiver.getId(), MessageStatus.SENT);

        for (ChatMessage message : unreadMessages) {
            message.setStatus(MessageStatus.SEEN);
            message.setSeenAt(Instant.now());
        }

        chatRepository.saveAll(unreadMessages);
    }

}
