package pe.edu.vallegrande.app.repository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.app.model.GeminiConversation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface GeminiConversationRepository extends ReactiveCrudRepository<GeminiConversation, Long> {

    Flux<GeminiConversation> findByStatus(String status);

    Mono<GeminiConversation> findByUserQuestion(String userQuestion);
}