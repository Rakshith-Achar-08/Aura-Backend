package com.project.aura.Controller;

import com.project.aura.DTO.ChatMessageRequest;
import com.project.aura.DTO.ChatMessageResponse;
import com.project.aura.DTO.ChatSessionDTO;
import com.project.aura.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * POST /api/chat/session/{userId}
     * Start a new chat session for the given user.
     */
    @PostMapping("/session/{userId}")
    public ResponseEntity<ChatSessionDTO> startSession(@PathVariable Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.startSession(userId));
    }

    /**
     * GET /api/chat/sessions/{userId}
     * List all chat sessions for a user (newest first).
     */
    @GetMapping("/sessions/{userId}")
    public ResponseEntity<List<ChatSessionDTO>> getUserSessions(@PathVariable Integer userId) {
        return ResponseEntity.ok(chatService.getUserSessions(userId));
    }

    /**
     * POST /api/chat/message/{sessionId}
     * Send a message; the response includes both the user message and the bot reply.
     * Body: { "messageText": "I have a headache" }
     */
    @PostMapping("/message/{sessionId}")
    public ResponseEntity<List<ChatMessageResponse>> sendMessage(
            @PathVariable Integer sessionId,
            @RequestBody ChatMessageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.sendMessage(sessionId, request));
    }

    /**
     * GET /api/chat/session/{sessionId}/messages
     * Get the full conversation history for a session.
     */
    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getSessionMessages(@PathVariable Integer sessionId) {
        return ResponseEntity.ok(chatService.getSessionMessages(sessionId));
    }
}
