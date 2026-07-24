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

/**
 * REST controller for retrieving chat conversations.
 */
@Controller
@AllArgsConstructor
@RequestMapping("api/v1")
public class ChatController {

    private final ChatService chatService;

    /**
     * Fetches chat messages between two users for a specific ad.
     *
     * @param senderId sender user id
     * @param receiverId receiver user id
     * @param adId ad id
     * @return list of chat messages
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> fetchChat(@RequestParam Long senderId,
                                       @RequestParam Long receiverId,
                                       @RequestParam Long adId) {
        return ResponseEntity.ok(chatService.fetchChat(senderId, receiverId, adId));
    }

    /**
     * Returns all active conversations for a user.
     *
     * @param userId user id
     * @return list of conversation summaries
     */
    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<ConversationSummary>> getUserConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }
}

