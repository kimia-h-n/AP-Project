package com.example.sales.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Summary view of a conversation shown in the chat list.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationSummary {
    private Long contactId;
    private String contactName;
    private String contactUsername;
    private String lastMessage;
    private Long adId;
    private String adTitle;
    private Long senderId;
}
