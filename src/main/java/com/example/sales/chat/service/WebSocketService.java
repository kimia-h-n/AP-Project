package com.example.sales.chat.service;


import com.example.sales.chat.ChatMapper;
import com.example.sales.chat.ChatMessage;
import com.example.sales.chat.dto.MessageRequest;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Instant;

@Service
@AllArgsConstructor
public class WebSocketService {

    private final UserRepository userRepository;
    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatMapper chatMapper;

    public void sendMessage(MessageRequest message, Principal principal) {
        User sender = userRepository.findByUsername(principal.getName()).orElseThrow(UserNotFoundException::new);
        User receiver = userRepository.findById(message.getReceiverId()).orElseThrow(UserNotFoundException::new);

        ChatMessage chatMessage = ChatMessage.builder()
                .sentAt(Instant.now())
                .message(message.getMessage())
                .sender(sender)
                .receiver(receiver)
                .build();

        chatService.saveChat(chatMessage);
        String receiverDestination = "/queue/messages-" + chatMessage.getReceiver().getId();
        simpMessagingTemplate.convertAndSend(receiverDestination, chatMapper.toResponse(chatMessage));
    }
}
