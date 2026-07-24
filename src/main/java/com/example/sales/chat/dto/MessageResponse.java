package com.example.sales.chat.dto;

import com.example.sales.chat.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO representing a chat message.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private Instant sentAt;
    private MessageStatus status;
    private Instant seenAt;
    private Long adId;
}
