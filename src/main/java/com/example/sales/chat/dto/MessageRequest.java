package com.example.sales.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket payload used to send a new chat message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private Long receiverId;
    private String message;
    private Long adId;
}
