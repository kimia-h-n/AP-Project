package com.example.sales.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository


public interface ChatRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
            SELECT m
            FROM ChatMessage m
            WHERE (m.sender.id = :user1Id AND m.receiver.id = :user2Id)
               OR (m.sender.id = :user2Id AND m.receiver.id = :user1Id)
            ORDER BY m.sentAt ASC
            """)
    List<ChatMessage> findChatMessagesBetweenUsers(
            @Param("user1Id") Long user1Id,
            @Param("user2Id") Long user2Id
    );

}
