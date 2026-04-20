package pe.edu.vallegrande.ApiAI.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface GoogleTranslateService {
    Mono<Map<String, Object>> translateText(String from, String to, String text);
    Flux<Map<String, Object>> getAllTranslations();
}
