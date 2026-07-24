package com.example.sales.chat;

import com.example.sales.chat.dto.MessageResponse;
import com.example.sales.chat.model.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for converting chat message entities to response DTOs.
 */
@Mapper(componentModel = "spring")
public interface ChatMapper {

    /**
     * Converts a chat message entity to a response DTO.
     *
     * @param chatMessage chat message entity
     * @return mapped response DTO
     */
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "adId", source = "ad.id")
    MessageResponse toResponse(ChatMessage chatMessage);

    /**
     * Converts a list of chat message entities to response DTOs.
     *
     * @param messageList list of chat message entities
     * @return mapped list of response DTOs
     */
    @Mapping(target = "senderId", source = "sender.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "message", source = "message")
    List<MessageResponse> toResponseList(List<ChatMessage> messageList);
}
