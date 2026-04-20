package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.ApiAI.service.GoogleTranslateService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/translate")
@RequiredArgsConstructor
@Tag(name = "Google Translate", description = "API para traducción de textos")
public class GoogleTranslateRest {

    private final GoogleTranslateService service;

    @PostMapping
    @Operation(
        summary = "Traducir texto", 
        description = "Traduce un texto de un idioma a otro",
        requestBody = @RequestBody(
            content = @Content(
                examples = @ExampleObject(
                    value = "{\"text\": \"Hola, ¿cómo estás?\"}"
                )
            )
        )
    )
    public Mono<ResponseEntity<Map<String, Object>>> translateText(
            @RequestParam(defaultValue = "auto") String from,
            @RequestParam String to,
            @org.springframework.web.bind.annotation.RequestBody Map<String, String> request) {
        
        String text = request.get("text");
        
        return service.translateText(from, to, text)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/history")
    @Operation(summary = "Historial de traducciones", description = "Obtiene todas las traducciones realizadas")
    public Flux<Map<String, Object>> getTranslationHistory() {
        return service.getAllTranslations();
    }
}
