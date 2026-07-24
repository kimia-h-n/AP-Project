package com.example.sales.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload used to mark a message as seen.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSeenRequest {
    Long messageId;
}
