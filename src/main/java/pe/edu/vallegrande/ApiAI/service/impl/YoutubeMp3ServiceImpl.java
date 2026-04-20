package pe.edu.vallegrande.ApiAI.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.ApiAI.model.YoutubeMp3;
import pe.edu.vallegrande.ApiAI.repository.YoutubeMp3Repository;
import pe.edu.vallegrande.ApiAI.service.YoutubeMp3Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoutubeMp3ServiceImpl implements YoutubeMp3Service {

    private final WebClient webClient;
    private final YoutubeMp3Repository repository;

    @Value("${rapidapi.youtube-mp3.url}")
    private String apiUrl;

    @Value("${rapidapi.youtube-mp3.host}")
    private String apiHost;

    @Value("${rapidapi.youtube-mp3.apikey}")
    private String apiKey;

    @Override
    public Mono<Map<String, Object>> downloadMp3(String url) {
        return webClient.get()
                .uri(apiUrl + "/download/mp3?url=" + url)
                .header("x-rapidapi-host", apiHost)
                .header("x-rapidapi-key", apiKey)
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(response -> {
                    YoutubeMp3 youtubeMp3 = new YoutubeMp3();
                    youtubeMp3.setVideoUrl(url);
                    
                    String downloadUrl = getStringValue(response, "downloadUrl", "link", "url", "download_url");
                    
                    youtubeMp3.setDownloadUrl(downloadUrl);
                    youtubeMp3.setStatus(downloadUrl.isEmpty() ? "failed" : "completed");
                    youtubeMp3.setCreatedAt(LocalDateTime.now());

                    return repository.save(youtubeMp3)
                            .map(saved -> {
                                Map<String, Object> result = new HashMap<>();
                                result.put("id", saved.getId());
                                result.put("videoUrl", saved.getVideoUrl());
                                result.put("downloadUrl", saved.getDownloadUrl());
                                result.put("status", saved.getStatus());
                                result.put("createdAt", saved.getCreatedAt());
                                return result;
                            });
                })
                .doOnError(error -> log.error("Error downloading MP3: {}", error.getMessage()));
    }
    
    private String getStringValue(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object value = map.get(key);
            if (value != null && !value.toString().isEmpty()) {
                return value.toString();
            }
        }
        return "";
    }

    @Override
    public Flux<Map<String, Object>> getAllDownloads() {
        return repository.findAll()
                .map(youtube -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("id", youtube.getId());
                    result.put("videoUrl", youtube.getVideoUrl());
                    result.put("downloadUrl", youtube.getDownloadUrl());
                    result.put("status", youtube.getStatus());
                    result.put("createdAt", youtube.getCreatedAt());
                    return result;
                })
                .doOnError(error -> log.error("Error getting downloads: {}", error.getMessage()));
    }
}
