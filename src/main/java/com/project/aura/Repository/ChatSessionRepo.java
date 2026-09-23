package com.project.aura.Repository;

import com.project.aura.Entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatSessionRepo extends JpaRepository<ChatSession, Integer> {

    List<ChatSession> findByUser_UseridOrderByStartedAtDesc(Integer userid);
}
