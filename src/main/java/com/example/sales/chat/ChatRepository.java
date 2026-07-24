package com.example.sales.chat;

import com.example.sales.chat.model.ChatMessage;
import com.example.sales.chat.model.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for chat message persistence and query operations.
 */
@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Finds all chat messages between two users for a specific ad.
     *
     * @param user1Id first user id
     * @param user2Id second user id
     * @param adId    ad id
     * @return ordered list of chat messages
     */
    @Query("""
            SELECT m
            FROM ChatMessage m
            WHERE m.ad.id = :adId
              AND ((m.sender.id = :user1Id AND m.receiver.id = :user2Id)
                   OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id))
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findChatMessagesBetweenUsersForAd(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id,
            @Param("adId") Long adId
    );

    /**
     * Finds the latest message for each active conversation of a user.
     *
     * @param userId user id
     * @return list of latest messages grouped by conversation
     */
    @Query("""
            SELECT m FROM ChatMessage m
            WHERE m.id IN (
                SELECT MAX(msg.id)
                FROM ChatMessage msg
                WHERE msg.sender.id = :userId OR msg.receiver.id = :userId
                GROUP BY 
                    CASE 
                        WHEN msg.sender.id = :userId THEN msg.receiver.id 
                        ELSE msg.sender.id 
                    END
            )
            ORDER BY m.sentAt DESC
            """)
    List<ChatMessage> findActiveConversationsForUser(@Param("userId") Long userId);

    /**
     * Finds messages with the specified sender, receiver, and status.
     *
     * @param senderId   sender id
     * @param receiverId receiver id
     * @param status     message status
     * @return matching chat messages
     */
    List<ChatMessage> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, MessageStatus status);
}
