package pe.edu.vallegrande.ApiAI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.youtube-mp3.url}")
    private String youtubeUrl;

    @Value("${rapidapi.youtube-mp3.host}")
    private String youtubeHost;

    @Value("${rapidapi.youtube-mp3.apikey}")
    private String youtubeApikey;

    @Value("${rapidapi.google-translate.url}")
    private String translateUrl;

    @Value("${rapidapi.google-translate.host}")
    private String translateHost;

    @Value("${rapidapi.google-translate.apikey}")
    private String translateApikey;

    @Bean(name = "youtubeWebClient")
    public WebClient youtubeWebClient() {
        return WebClient.builder()
                .baseUrl(youtubeUrl)
                .defaultHeader("x-rapidapi-host", youtubeHost)
                .defaultHeader("x-rapidapi-key", youtubeApikey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean(name = "translateWebClient")
    public WebClient translateWebClient() {
        return WebClient.builder()
                .baseUrl(translateUrl)
                .defaultHeader("x-rapidapi-host", translateHost)
                .defaultHeader("x-rapidapi-key", translateApikey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
