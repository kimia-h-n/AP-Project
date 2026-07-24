package com.example.sales.chat.controller;


import com.example.sales.chat.dto.MessageRequest;
import com.example.sales.chat.dto.UpdateSeenRequest;
import com.example.sales.chat.service.WebSocketService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * WebSocket controller for chat message delivery and conversation state updates.
 */
@Controller
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class WebSocketController {

    private final WebSocketService webSocketService;

    /**
     * Sends a chat message over WebSocket.
     *
     * @param message   outgoing message payload
     * @param principal authenticated user principal
     */
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageRequest message, Principal principal) {
        webSocketService.sendMessage(message, principal);
    }

    /**
     * Marks a conversation as seen.
     *
     * @param senderId  sender user id whose messages are being marked as seen
     * @param principal authenticated user principal
     */
    @MessageMapping("/seen")
    public void seenConversation(@Payload Long senderId, Principal principal) {
        webSocketService.markConversationSeen(senderId, principal);
    }
}
