package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.ApiAI.service.YoutubeMp3Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
@Tag(name = "YouTube MP3", description = "API para descargar videos de YouTube en formato MP3")
public class YoutubeMp3Rest {

    private final YoutubeMp3Service service;

    @GetMapping("/download")
    @Operation(summary = "Descargar MP3", description = "Descarga un video de YouTube en formato MP3")
    public Mono<ResponseEntity<Map<String, Object>>> downloadMp3(@RequestParam String url) {
        return service.downloadMp3(url)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    @Operation(summary = "Historial de descargas", description = "Obtiene todas las descargas realizadas")
    public Flux<Map<String, Object>> getDownloadHistory() {
        return service.getAllDownloads();
    }
}
