package com.example.sales.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConversationSummary {
    private Long contactId;
    private String contactName;      // نام مخاطبی که با او چت شده
    private String contactUsername;
    // private String contactAvatarUrl; // در صورت نیاز برای تصویر پروفایل دیوار
    private String lastMessage;
    private Long adId;
    private String adTitle;          // عنوان همان آگهی، برای نمایش در فرانت
    //    private LocalDateTime lastMessageTime;
    private Long senderId;           // برای اینکه مشخص شود آخرین پیام را چه کسی فرستاده
}
