package pe.edu.vallegrande.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "gemini_conversation")
public class GeminiConversation {

    @Id
    @Column(value = "id_conversation")
    private Long idConversation;

    @Column(value = "user_question")
    private String userQuestion;

    @Column(value = "ai_response")
    private String aiResponse;

    @Column(value = "conversation_history")
    private String conversationHistory;

    @Column(value = "api_used")
    private String apiUsed = "Gemini Pro";

    @Column(value = "response_raw")
    private String responseRaw;

    @Column(value = "status")
    private String status = "A";

}