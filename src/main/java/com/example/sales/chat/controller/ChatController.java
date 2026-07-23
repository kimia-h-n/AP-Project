package com.example.sales.chat.controller;


import com.example.sales.chat.dto.ConversationSummary;
import com.example.sales.chat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("api/v1")
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<?> fetchChat(@RequestParam Long senderId, @RequestParam Long receiverId
    ,@RequestParam Long adId) {
        return ResponseEntity.ok(
                chatService.fetchChat(senderId, receiverId, adId));

    }
    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<ConversationSummary>> getUserConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }


}
