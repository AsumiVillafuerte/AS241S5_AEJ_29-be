package pe.edu.vallegrande.app.service;

import org.springframework.http.codec.multipart.FilePart;
import pe.edu.vallegrande.app.model.ImgRemoval;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ImgRemovalService {
    Mono<ImgRemoval> procesarImagen(FilePart imageFile);
    Mono<ImgRemoval> create(ImgRemoval imgRemoval);
    Flux<ImgRemoval> findAll();
    Mono<ImgRemoval> findById(Long id);
    Mono<ImgRemoval> deleteLogical(Long id);
    Mono<ImgRemoval> reactivate(Long id);
    Mono<Void> deletePhysical(Long id);
    Flux<ImgRemoval> getHistory();
    Flux<ImgRemoval> findByStatus(String status);
}
