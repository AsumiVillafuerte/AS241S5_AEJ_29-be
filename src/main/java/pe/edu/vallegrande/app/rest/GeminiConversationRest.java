package pe.edu.vallegrande.app.rest;

import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.app.model.GeminiConversation;
import pe.edu.vallegrande.app.service.GeminiConversationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/gemini")
public class GeminiConversationRest {

    private final GeminiConversationService service;

    public GeminiConversationRest(GeminiConversationService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<GeminiConversation> findAll() {
        return service.getAllConversations();
    }

    @GetMapping("/{id}")
    public Mono<GeminiConversation> findById(@PathVariable Long id) {
        return service.getConversationById(id);
    }

    @PostMapping("/chat")
    public Mono<GeminiConversation> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        return service.chatWithGemini(question);
    }

    @GetMapping("/status/{status}")
    public Flux<GeminiConversation> findByStatus(@PathVariable String status) {
        return service.getConversationsByStatus(status);
    }

    @GetMapping("/question/{question}")
    public Mono<GeminiConversation> findByUserQuestion(@PathVariable String question) {
        return service.getConversationByUserQuestion(question);
    }
}