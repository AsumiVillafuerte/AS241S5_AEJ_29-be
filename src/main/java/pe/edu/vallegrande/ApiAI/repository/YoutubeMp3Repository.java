package pe.edu.vallegrande.ApiAI.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.ApiAI.model.YoutubeMp3;

@Repository
public interface YoutubeMp3Repository extends ReactiveCrudRepository<YoutubeMp3, Long> {
}
