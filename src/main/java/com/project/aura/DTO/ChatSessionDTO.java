package com.project.aura.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatSessionDTO {

    private Integer sessionId;
    private Integer userId;
    private LocalDateTime startedAt;
    private Integer messageCount;
}
