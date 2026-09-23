package com.project.aura.Repository;

import com.project.aura.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepo extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findByChatSession_SessionIdOrderBySentAt(Integer sessionId);
}
