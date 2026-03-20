package com.example.aichatbot.service;

import com.example.aichatbot.dto.*;
import com.example.aichatbot.entity.*;
import com.example.aichatbot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private GroqAIService groqAIService;

    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    public ChatResponse chat(ChatRequest request,
                             String username) {

        // Step 1: Get user from DB
        User user = userRepository
            .findByUsername(username)
            .orElseThrow(() ->
                new RuntimeException("User not found"));

        // Step 2: Get last 10 messages for context
        List<ChatHistory> history =
            chatHistoryRepository
                .findTop10BySessionIdOrderByCreatedAtDesc(
                    request.getSessionId());

        // Step 3: Build messages list for Claude
        // (reverse so oldest first)
        Collections.reverse(history);
        List<Map<String, String>> messages =
            new ArrayList<>();

        // Add history
        for (ChatHistory chat : history) {
            messages.add(Map.of(
                "role", "user",
                "content", chat.getUserMessage()));
            messages.add(Map.of(
                "role", "assistant",
                "content", chat.getAiResponse()));
        }

        // Add current message
        messages.add(Map.of(
            "role", "user",
            "content", request.getMessage()));

        // Step 4: Get AI response
        String aiResponse = groqAIService.getAIResponse(messages);

        // Step 5: Save to DB
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setSessionId(request.getSessionId());
        chatHistory.setUserMessage(request.getMessage());
        chatHistory.setAiResponse(aiResponse);
        chatHistory.setUser(user);
        chatHistoryRepository.save(chatHistory);

        return new ChatResponse(
            aiResponse,
            request.getSessionId(),
            true);
    }

    public List<ChatHistory> getHistory(
            String sessionId) {
        return chatHistoryRepository
            .findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}