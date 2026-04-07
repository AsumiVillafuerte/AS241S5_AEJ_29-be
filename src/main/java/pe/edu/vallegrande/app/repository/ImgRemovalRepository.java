package pe.edu.vallegrande.app.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.edu.vallegrande.app.model.ImgRemoval;
import reactor.core.publisher.Flux;

public interface ImgRemovalRepository extends ReactiveCrudRepository<ImgRemoval, Long> {

    Flux<ImgRemoval> findAllByOrderByCreatedAtDesc();

    Flux<ImgRemoval> findByStatus(String status);
}