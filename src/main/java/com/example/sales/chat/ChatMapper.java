package com.example.sales.chat;

import com.example.sales.chat.dto.MessageResponse;
import com.example.sales.chat.model.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "adId", source = "ad.id")
    MessageResponse toResponse(ChatMessage chatMessage);

    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    List<MessageResponse> toResponseList(List<ChatMessage> messageList);
}
