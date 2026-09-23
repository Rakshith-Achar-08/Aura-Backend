package com.project.aura.DTO;

import com.project.aura.Entity.ChatMessage;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageResponse {

    private Integer messageId;
    private Integer sessionId;
    private ChatMessage.SenderType sender;
    private String messageText;
    private LocalDateTime sentAt;
}
