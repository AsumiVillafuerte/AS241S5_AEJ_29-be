package pe.edu.vallegrande.app.service;

import pe.edu.vallegrande.app.model.GeminiConversation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GeminiConversationService {

    Mono<GeminiConversation> chatWithGemini(String question);

    Flux<GeminiConversation> getAllConversations();

    Mono<GeminiConversation> getConversationById(Long id);

    Flux<GeminiConversation> getConversationsByStatus(String status);


    Mono<GeminiConversation> getConversationByUserQuestion(String question);


    Flux<GeminiConversation> getConversationHistory();
}