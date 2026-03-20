package com.example.aichatbot.controller;

import com.example.aichatbot.dto.*;
import com.example.aichatbot.entity.ChatHistory;
import com.example.aichatbot.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ChatResponse> sendMessage(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        ChatResponse response = chatService.chat(
            request, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatHistory>> getHistory(
            @PathVariable String sessionId) {
        return ResponseEntity.ok(
            chatService.getHistory(sessionId));
    }
}