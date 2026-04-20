package pe.edu.vallegrande.ApiAI.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.ApiAI.model.GoogleTranslate;
import pe.edu.vallegrande.ApiAI.repository.GoogleTranslateRepository;
import pe.edu.vallegrande.ApiAI.service.GoogleTranslateService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleTranslateServiceImpl implements GoogleTranslateService {

    private final WebClient webClient;
    private final GoogleTranslateRepository repository;

    @Value("${rapidapi.google-translate.url}")
    private String apiUrl;

    @Value("${rapidapi.google-translate.host}")
    private String apiHost;

    @Value("${rapidapi.google-translate.apikey}")
    private String apiKey;

    @Override
    public Mono<Map<String, Object>> translateText(String from, String to, String text) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("from", from);
        requestBody.put("to", to);
        requestBody.put("text", text);

        return webClient.post()
                .uri(apiUrl + "/api/v1/translator/text")
                .header("x-rapidapi-host", apiHost)
                .header("x-rapidapi-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    GoogleTranslate translate = new GoogleTranslate();
                    translate.setTextOriginal(text);
                    translate.setLanguageFrom(from);
                    translate.setLanguageTo(to);
                    translate.setTextTranslated(response.get("trans").toString());
                    translate.setCreatedAt(LocalDateTime.now());

                    return repository.save(translate)
                            .map(saved -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("id", saved.getId());
                                result.put("original", saved.getTextOriginal());
                                result.put("translated", saved.getTextTranslated());
                                result.put("from", saved.getLanguageFrom());
                                result.put("to", saved.getLanguageTo());
                                result.put("createdAt", saved.getCreatedAt());
                                return result;
                            });
                })
                .doOnError(error -> log.error("Error translating text: {}", error.getMessage()));
    }

    @Override
    public Flux<Map<String, Object>> getAllTranslations() {
        return repository.findAll()
                .map(translate -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", translate.getId());
                    result.put("original", translate.getTextOriginal());
                    result.put("translated", translate.getTextTranslated());
                    result.put("from", translate.getLanguageFrom());
                    result.put("to", translate.getLanguageTo());
                    result.put("createdAt", translate.getCreatedAt());
                    return result;
                })
                .doOnError(error -> log.error("Error getting translations: {}", error.getMessage()));
    }
}
