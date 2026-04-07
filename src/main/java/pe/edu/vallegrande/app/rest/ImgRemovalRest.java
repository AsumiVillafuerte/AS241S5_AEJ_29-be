package pe.edu.vallegrande.app.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.app.model.ImgRemoval;
import pe.edu.vallegrande.app.service.ImgRemovalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/img-removal")
public class ImgRemovalRest {

    private final ImgRemovalService service;

    public ImgRemovalRest(ImgRemovalService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ImgRemoval> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ImgRemoval> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping(value = "/process", consumes = "multipart/form-data")
    public Mono<ImgRemoval> procesarImagen(@RequestPart("image") FilePart imageFile) {
        return service.procesarImagen(imageFile);
    }

    @DeleteMapping("/logical/{id}")
    public Mono<ImgRemoval> deleteLogical(@PathVariable Long id) {
        return service.deleteLogical(id);
    }

    @PutMapping("/reactivate/{id}")
    public Mono<ImgRemoval> reactivate(@PathVariable Long id) {
        return service.reactivate(id);
    }

    @DeleteMapping("/physical/{id}")
    public Mono<Void> deletePhysical(@PathVariable Long id) {
        return service.deletePhysical(id);
    }

    @GetMapping("/history")
    public Flux<ImgRemoval> history() {
        return service.getHistory();
    }

    @GetMapping("/status/{status}")
    public Flux<ImgRemoval> findByStatus(@PathVariable String status) {
        return service.findByStatus(status);
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public Mono<ResponseEntity<byte[]>> getResultImage(@PathVariable Long id) {
        return service.findById(id)
                .flatMap(imgRemoval -> {
                    String resultUrl = imgRemoval.getResultUrl();
                    if (resultUrl != null && resultUrl.startsWith("/uploads/")) {
                        try {
                            Path imagePath = Paths.get("." + resultUrl);
                            if (Files.exists(imagePath)) {
                                byte[] imageBytes = Files.readAllBytes(imagePath);
                                return Mono.just(ResponseEntity.ok()
                                        .contentType(MediaType.IMAGE_PNG)
                                        .body(imageBytes));
                            }
                        } catch (Exception e) {
                            return Mono.just(ResponseEntity.notFound().build());
                        }
                    }
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }
}
