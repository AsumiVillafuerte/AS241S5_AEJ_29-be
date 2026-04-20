package pe.edu.vallegrande.ApiAI.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.ApiAI.model.GoogleTranslate;

@Repository
public interface GoogleTranslateRepository extends ReactiveCrudRepository<GoogleTranslate, Long> {
}
