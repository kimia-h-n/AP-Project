package com.example.sales.chat.service;


import com.example.sales.chat.ChatMapper;
import com.example.sales.chat.ChatMessage;
import com.example.sales.chat.ChatRepository;
import com.example.sales.chat.dto.MessageRequest;
import com.example.sales.chat.dto.MessageResponse;
import com.example.sales.exception.UserNotFoundException;
import com.example.sales.repository.UserRepository;
import com.example.sales.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;

    public List<MessageResponse> fetchChat(Long senderId, Long receiverId) {
        return chatMapper.toResponseList(chatRepository.findChatMessagesBetweenUsers(senderId, receiverId));
    }


    public void saveChat(ChatMessage chatMessage) {
        chatRepository.save(chatMessage);
    }
}
