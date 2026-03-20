package com.example.aichatbot.dto;

import lombok.Data;

@Data
public class ChatResponse {
    private String response;
    private String sessionId;
    private boolean success;

    public ChatResponse(String response, 
                        String sessionId, 
                        boolean success) {
        this.response = response;
        this.sessionId = sessionId;
        this.success = success;
    }
}