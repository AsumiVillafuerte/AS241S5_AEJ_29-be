package pe.edu.vallegrande.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.app.model.GeminiConversation;
import pe.edu.vallegrande.app.repository.GeminiConversationRepository;
import pe.edu.vallegrande.app.service.GeminiConversationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiConversationServiceImpl implements GeminiConversationService {

    private final GeminiConversationRepository repository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    @Value("${rapidapi.url}")
    private String apiUrl;

    @Autowired
    public GeminiConversationServiceImpl(GeminiConversationRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<GeminiConversation> chatWithGemini(String question) {
        GeminiConversation conversation = new GeminiConversation();
        conversation.setUserQuestion(question);
        conversation.setStatus("A");
        conversation.setApiUsed("Gemini Pro");

        Map<String, Object> requestBody = buildGeminiRequest(question);

        return webClient.post()
                .uri(apiUrl)
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", apiHost)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(response -> {
                    String aiResponse = extractTextFromResponse(response);
                    conversation.setAiResponse(aiResponse);
                    conversation.setResponseRaw(response);
                    return repository.save(conversation);
                })
                .onErrorResume(error -> {
                    conversation.setStatus("I");
                    conversation.setResponseRaw(error.getMessage());
                    conversation.setAiResponse("Error al procesar la solicitud");
                    return repository.save(conversation);
                });
    }

    @Override
    public Flux<GeminiConversation> getAllConversations() {
        return repository.findAll();
    }

    @Override
    public Mono<GeminiConversation> getConversationById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Flux<GeminiConversation> getConversationsByStatus(String status) {
        return repository.findByStatus(status);
    }

    @Override
    public Mono<GeminiConversation> getConversationByUserQuestion(String question) {
        return repository.findByUserQuestion(question);
    }

    @Override
    public Flux<GeminiConversation> getConversationHistory() {
        return repository.findAll();
    }

    private Map<String, Object> buildGeminiRequest(String question) {
        Map<String, Object> request = new HashMap<>();

        Map<String, Object> content = new HashMap<>();
        content.put("role", "user");

        Map<String, String> part = new HashMap<>();
        part.put("text", question);
        content.put("parts", List.of(part));

        request.put("contents", List.of(content));

        return request;
    }

    private String extractTextFromResponse(String response) {
        try {
            JsonNode node = objectMapper.readTree(response);

            JsonNode candidates = node.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    JsonNode text = parts.get(0).path("text");
                    if (!text.isMissingNode()) {
                        return text.asText();
                    }
                }
            }

            return "Respuesta procesada exitosamente";

        } catch (Exception e) {
            return "Error al extraer respuesta: " + e.getMessage();
        }
    }
}