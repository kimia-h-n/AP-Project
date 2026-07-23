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

@Controller
@RequestMapping("/api/v1/")
@AllArgsConstructor
public class WebSocketController {

    private final WebSocketService webSocketService;


    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageRequest message, Principal principal) {
        webSocketService.sendMessage(message, principal);
    }

    @MessageMapping("/seen")
    public void seenConversation(@Payload Long senderId, Principal principal) {
        webSocketService.markConversationSeen(senderId, principal);
    }
}
