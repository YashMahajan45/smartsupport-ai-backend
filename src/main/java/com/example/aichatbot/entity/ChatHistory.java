package com.example.aichatbot.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_message", 
            columnDefinition = "TEXT",
            nullable = false)
    private String userMessage;

    @Column(name = "ai_response",
            columnDefinition = "TEXT",
            nullable = false)
    private String aiResponse;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}