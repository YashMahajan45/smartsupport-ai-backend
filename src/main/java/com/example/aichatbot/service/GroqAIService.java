package com.example.aichatbot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GroqAIService {

    @Value("${groq.api.key}")
    private String apiKey;

    private static final String GROQ_URL =
        "https://api.groq.com/openai/v1/chat/completions";

    private static final String SYSTEM_PROMPT =
        "You are a helpful customer support agent. " +
        "Be polite, concise and professional. " +
        "Help users with their queries. " +
        "If you don't know something, say so honestly.";

    public String getAIResponse(
            List<Map<String, String>> messages) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Add system message at beginning
        List<Map<String, String>> allMessages = new ArrayList<>();
        allMessages.add(Map.of(
            "role", "system",
            "content", SYSTEM_PROMPT));
        allMessages.addAll(messages);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", allMessages);
        body.put("max_tokens", 1000);

        HttpEntity<Map<String, Object>> entity =
            new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response =
                restTemplate.postForEntity(
                    GROQ_URL, entity, Map.class);

            List<Map> choices = (List<Map>)
                response.getBody().get("choices");
            Map message = (Map) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (Exception e) {
            System.out.println("Groq API Error: " + e.getMessage());
            return "I'm sorry, I'm temporarily unavailable. " +
                   "Please try again in a moment.";
        }
    }
}