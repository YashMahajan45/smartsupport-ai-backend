package com.example.aichatbot.repository;

import com.example.aichatbot.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatHistoryRepository 
        extends JpaRepository<ChatHistory, Long> {

    // Get all chats for a session
    List<ChatHistory> findBySessionIdOrderByCreatedAtAsc(
            String sessionId);

    // Get last 10 messages of a session
    List<ChatHistory> findTop10BySessionIdOrderByCreatedAtDesc(
            String sessionId);

    // Get all chats of a user
    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(
            Long userId);
}