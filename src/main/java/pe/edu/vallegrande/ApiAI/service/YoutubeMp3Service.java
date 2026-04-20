package pe.edu.vallegrande.ApiAI.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface YoutubeMp3Service {
    Mono<Map<String, Object>> downloadMp3(String url);
    Flux<Map<String, Object>> getAllDownloads();
}
