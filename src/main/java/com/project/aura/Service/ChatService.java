package com.project.aura.Service;

import com.project.aura.DTO.ChatMessageRequest;
import com.project.aura.DTO.ChatMessageResponse;
import com.project.aura.DTO.ChatSessionDTO;
import com.project.aura.Entity.ChatMessage;
import com.project.aura.Entity.ChatSession;
import com.project.aura.Entity.Users;
import com.project.aura.Exception.ResourceNotFoundException;
import com.project.aura.Repository.ChatMessageRepo;
import com.project.aura.Repository.ChatSessionRepo;
import com.project.aura.Repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ChatService — manages chat sessions and messages with a keyword-based
 * auto-response engine. Replace generateAutoResponse() with a call to
 * OpenAI / Gemini API for production-grade AI responses.
 */
@Service
public class ChatService {

    @Autowired
    private ChatSessionRepo chatSessionRepo;

    @Autowired
    private ChatMessageRepo chatMessageRepo;

    @Autowired
    private UserRepo userRepo;

    // ── Session Management ───────────────────────────────────────────────────────

    public ChatSessionDTO startSession(Integer userId) {
        Users user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        ChatSession session = ChatSession.builder().user(user).build();
        ChatSession saved = chatSessionRepo.save(session);

        return toSessionDTO(saved, 0);
    }

    public List<ChatSessionDTO> getUserSessions(Integer userId) {
        return chatSessionRepo.findByUser_UseridOrderByStartedAtDesc(userId).stream()
                .map(s -> toSessionDTO(s,
                        chatMessageRepo.findByChatSession_SessionIdOrderBySentAt(s.getSessionId()).size()))
                .collect(Collectors.toList());
    }

    // ── Messaging ────────────────────────────────────────────────────────────────

    /**
     * Send a user message and immediately get a bot auto-reply.
     * Returns both the user message and bot response in order.
     */
    public List<ChatMessageResponse> sendMessage(Integer sessionId, ChatMessageRequest request) {
        ChatSession session = chatSessionRepo.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session not found: " + sessionId));

        // Persist user message
        ChatMessage userMsg = ChatMessage.builder()
                .chatSession(session)
                .sender(ChatMessage.SenderType.USER)
                .messageText(request.getMessageText())
                .build();
        ChatMessage savedUserMsg = chatMessageRepo.save(userMsg);

        // Generate and persist bot response
        String botText = generateAutoResponse(request.getMessageText());
        ChatMessage botMsg = ChatMessage.builder()
                .chatSession(session)
                .sender(ChatMessage.SenderType.BOT)
                .messageText(botText)
                .build();
        ChatMessage savedBotMsg = chatMessageRepo.save(botMsg);

        return List.of(toMessageResponse(savedUserMsg), toMessageResponse(savedBotMsg));
    }

    public List<ChatMessageResponse> getSessionMessages(Integer sessionId) {
        if (!chatSessionRepo.existsById(sessionId)) {
            throw new ResourceNotFoundException("Chat session not found: " + sessionId);
        }
        return chatMessageRepo.findByChatSession_SessionIdOrderBySentAt(sessionId)
                .stream()
                .map(this::toMessageResponse)
                .collect(Collectors.toList());
    }

    // ── Keyword-Based Auto-Response Engine ───────────────────────────────────────

    /**
     * Maps common medical keywords to helpful responses.
     * Extend this method or replace with an AI API call (Gemini/OpenAI) for production.
     */
    private String generateAutoResponse(String text) {
        String lower = text.toLowerCase();

        if (lower.contains("fever") || lower.contains("temperature") || lower.contains("pyrexia")) {
            return "🌡️ Fever can indicate infection. Stay hydrated and rest. Take paracetamol if needed. " +
                   "See a doctor if fever exceeds 103°F (39.4°C) or lasts more than 3 days.";
        }
        if (lower.contains("headache") || lower.contains("migraine")) {
            return "🧠 Headaches can be caused by stress, dehydration, or lack of sleep. Rest in a quiet, " +
                   "dark room and stay hydrated. See a doctor if the headache is severe or sudden.";
        }
        if (lower.contains("cough") || lower.contains("cold") || lower.contains("flu")) {
            return "😷 Coughs and colds usually resolve within 7–10 days. Stay hydrated, rest, and " +
                   "consider steam inhalation. See a doctor if symptoms worsen or persist beyond 2 weeks.";
        }
        if (lower.contains("chest pain") || lower.contains("heart attack") || lower.contains("palpitation")) {
            return "❤️ Chest pain can be serious! If you have sudden chest pain with breathlessness, " +
                   "sweating, or pain radiating to the arm/jaw — call emergency services (108/911) IMMEDIATELY!";
        }
        if (lower.contains("sos") || lower.contains("emergency") || lower.contains("ambulance") || lower.contains("help")) {
            return "🚨 Emergency situation detected! Use the SOS button immediately to alert nearby hospitals " +
                   "with your location. You can also call 108 (India) or 911 (USA) for ambulance services.";
        }
        if (lower.contains("diabetes") || lower.contains("blood sugar") || lower.contains("insulin") || lower.contains("glucose")) {
            return "💉 Diabetes requires consistent management — monitor blood sugar regularly, follow your " +
                   "medication schedule, maintain a balanced diet, and exercise regularly. Consult your endocrinologist.";
        }
        if (lower.contains("blood pressure") || lower.contains("hypertension") || lower.contains("bp")) {
            return "💊 High blood pressure (hypertension) is manageable with medication, a low-sodium diet, " +
                   "regular exercise, stress reduction, and regular monitoring. Always consult your doctor.";
        }
        if (lower.contains("allergy") || lower.contains("rash") || lower.contains("itching") || lower.contains("hives")) {
            return "🤧 Mild allergic reactions (rash, itching) can be managed with antihistamines. " +
                   "For severe reactions — throat swelling, difficulty breathing — seek emergency care immediately!";
        }
        if (lower.contains("pregnancy") || lower.contains("pregnant") || lower.contains("prenatal")) {
            return "🤰 Regular prenatal checkups are essential. Maintain a nutritious diet, take prescribed " +
                   "folic acid and iron supplements, stay hydrated, and attend all scheduled doctor appointments.";
        }
        if (lower.contains("mental health") || lower.contains("anxiety") || lower.contains("depression") || lower.contains("stress")) {
            return "🧘 Mental health is as important as physical health. Practice mindfulness, maintain social " +
                   "connections, exercise regularly, and don't hesitate to seek professional counselling or therapy.";
        }
        if (lower.contains("appointment") || lower.contains("doctor") || lower.contains("consult")) {
            return "📅 To book an appointment, contact your nearest hospital. Use our Hospital Finder feature " +
                   "to locate hospitals and their contact numbers. Early consultation prevents complications!";
        }
        if (lower.contains("medicine") || lower.contains("medication") || lower.contains("drug") || lower.contains("tablet")) {
            return "💊 Never self-medicate with prescription drugs. Always follow your doctor's prescription " +
                   "exactly. Consult a pharmacist for over-the-counter medicines.";
        }
        if (lower.contains("predict") || lower.contains("symptom") || lower.contains("diagnos")) {
            return "🏥 Use our Disease Prediction feature to check possible conditions based on your symptoms. " +
                   "Select your symptoms and our AI model will provide an assessment. Always verify with a doctor!";
        }
        if (lower.contains("vaccine") || lower.contains("vaccination") || lower.contains("immuniz")) {
            return "💉 Vaccinations are the best defence against infectious diseases. Ensure your immunization " +
                   "schedule is up to date. Consult your doctor or nearest health centre for available vaccines.";
        }
        if (lower.contains("covid") || lower.contains("coronavirus") || lower.contains("pandemic")) {
            return "😷 For COVID-19 symptoms (fever, dry cough, fatigue, loss of taste/smell), isolate yourself " +
                   "and get tested immediately. Follow local health authority guidelines for treatment and isolation.";
        }

        // Default fallback
        return "👨‍⚕️ Thank you for your query! I can assist with general health information. " +
               "For specific medical advice, diagnosis, or treatment, please consult a qualified healthcare " +
               "professional. Is there a specific symptom or health topic you'd like to know more about?";
    }

    // ── Mappers ──────────────────────────────────────────────────────────────────

    private ChatSessionDTO toSessionDTO(ChatSession s, int messageCount) {
        return ChatSessionDTO.builder()
                .sessionId(s.getSessionId())
                .userId(s.getUser().getUserid())
                .startedAt(s.getStartedAt())
                .messageCount(messageCount)
                .build();
    }

    private ChatMessageResponse toMessageResponse(ChatMessage m) {
        return ChatMessageResponse.builder()
                .messageId(m.getMessageId())
                .sessionId(m.getChatSession().getSessionId())
                .sender(m.getSender())
                .messageText(m.getMessageText())
                .sentAt(m.getSentAt())
                .build();
    }
}
