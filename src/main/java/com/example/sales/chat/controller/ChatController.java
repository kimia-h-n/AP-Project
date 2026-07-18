package com.example.sales.chat.controller;


import com.example.sales.chat.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@NoArgsConstructor
@AllArgsConstructor

@RequestMapping("api/v1")
public class ChatController {
    private ChatService chatService;

    @GetMapping("/conversations")
    public ResponseEntity<?> fetchChat(@RequestParam Long senderId, @RequestParam Long receiverId){
        return ResponseEntity.ok(
                chatService.fetchChat(senderId, receiverId));
    }
}
