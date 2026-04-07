package pe.edu.vallegrande.app.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.app.model.ImgRemoval;
import pe.edu.vallegrande.app.repository.ImgRemovalRepository;
import pe.edu.vallegrande.app.service.ImgRemovalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

@Service
public class ImgRemovalServiceImpl implements ImgRemovalService {

    private final ImgRemovalRepository repository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${rapidapi.key}")
    private String apiKey;

    @Value("${rapidapi.host}")
    private String apiHost;

    @Value("${rapidapi.url}")
    private String apiUrl;

    @Autowired
    public ImgRemovalServiceImpl(ImgRemovalRepository repository, WebClient.Builder builder) {
        this.repository = repository;
        this.webClient = builder.build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Mono<ImgRemoval> procesarImagen(FilePart imageFile) {

        ImgRemoval imgRemoval = new ImgRemoval();
        imgRemoval.setImageUrl(imageFile.filename());


        return imageFile.content()
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return bytes;
                })
                .collectList()
                .map(bytesList -> {
                    int totalSize = bytesList.stream().mapToInt(bytes -> bytes.length).sum();
                    byte[] imageBytes = new byte[totalSize];
                    int offset = 0;
                    for (byte[] bytes : bytesList) {
                        System.arraycopy(bytes, 0, imageBytes, offset, bytes.length);
                        offset += bytes.length;
                    }
                    return imageBytes;
                })
                .flatMap(imageBytes -> {
                    // Construir el multipart/form-data con la imagen binaria
                    MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
                    bodyBuilder.part("image", imageBytes)
                            .header("Content-Disposition", "form-data; name=image; filename=" + imageFile.filename())
                            .contentType(MediaType.IMAGE_JPEG);

                    return webClient.post()
                            .uri(apiUrl)
                            .header("x-rapidapi-key", apiKey)
                            .header("x-rapidapi-host", apiHost)
                            .contentType(MULTIPART_FORM_DATA)
                            .bodyValue(bodyBuilder.build())
                            .retrieve()
                            .bodyToMono(String.class)
                            .flatMap(response -> {
                                String resultInfo = extractAndSaveImage(response, imgRemoval);

                                imgRemoval.setResponseRaw(response);
                                imgRemoval.setResultUrl(resultInfo);
                                imgRemoval.setStatus("A");
                                imgRemoval.setApiUsed("RapidAPI - Background Removal");
                                imgRemoval.setCreatedAt(LocalDateTime.now());

                                return repository.save(imgRemoval);
                            });
                })
                .onErrorResume(error -> {
                    imgRemoval.setStatus("I");
                    imgRemoval.setResponseRaw(error.getMessage());
                    imgRemoval.setResultUrl("Error al procesar");
                    imgRemoval.setApiUsed("RapidAPI - Background Removal");
                    imgRemoval.setCreatedAt(LocalDateTime.now());

                    return repository.save(imgRemoval);
                });
    }


    @Override
    public Mono<ImgRemoval> create(ImgRemoval imgRemoval) {
        imgRemoval.setCreatedAt(LocalDateTime.now());
        imgRemoval.setStatus("A");
        return repository.save(imgRemoval);
    }

    @Override
    public Flux<ImgRemoval> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<ImgRemoval> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Mono<ImgRemoval> deleteLogical(Long id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus("I");
                    return repository.save(existing);
                });
    }


    @Override
    public Mono<ImgRemoval> reactivate(Long id) {
        return repository.findById(id)
                .flatMap(existing -> {
                    existing.setStatus("A");
                    return repository.save(existing);
                });
    }

    @Override
    public Mono<Void> deletePhysical(Long id) {
        return repository.deleteById(id);
    }

    @Override
    public Flux<ImgRemoval> getHistory() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Flux<ImgRemoval> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    // 🔧 Método auxiliar para extraer y guardar la imagen de la respuesta
    private String extractAndSaveImage(String response, ImgRemoval imgRemoval) {
        try {
            JsonNode node = objectMapper.readTree(response);

            int code = node.path("code").asInt();
            if (code == 0) {
                JsonNode base64Node = node.path("result_base64");
                if (!base64Node.isMissingNode()) {
                    return saveBase64Image(base64Node.asText());
                }
            }

            JsonNode statusNode = node.path("status");
            if (statusNode.isTextual() && "success".equals(statusNode.asText())) {
                JsonNode dataNode = node.path("data");
                JsonNode base64Node = dataNode.path("result_base64");
                if (!base64Node.isMissingNode()) {
                    return saveBase64Image(base64Node.asText());
                }

                JsonNode responseDataNode = node.path("responseData").path("data");
                if (responseDataNode.isArray() && responseDataNode.size() > 0) {
                    for (JsonNode item : responseDataNode) {
                        JsonNode imgNode = item.path("result_base64");
                        if (!imgNode.isMissingNode()) {
                            return saveBase64Image(imgNode.asText());
                        }
                    }
                }
            }

            if (response.contains("result_base64")) {
                JsonNode base64Node = node.path("result_base64");
                if (!base64Node.isMissingNode()) {
                    return saveBase64Image(base64Node.asText());
                }
            }

            JsonNode urlNode = node.path("result_url");
            if (!urlNode.isMissingNode()) {
                return urlNode.asText();
            }

            urlNode = node.path("url");
            if (!urlNode.isMissingNode()) {
                return urlNode.asText();
            }

            // Si todo falla, guardar mensaje de éxito
            String message = node.path("message").asText();
            if (!message.isEmpty()) {
                return "Éxito: " + message;
            }

            return "Imagen procesada exitosamente";

        } catch (Exception e) {
            return "Error al procesar imagen: " + e.getMessage();
        }
    }


    private String saveBase64Image(String base64Image) {
        try {
            // Decodificar base64 a bytes
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            Path uploadDir = Paths.get("uploads");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID().toString() + ".png";
            Path imagePath = uploadDir.resolve(fileName);


            Files.write(imagePath, imageBytes);


            return "/uploads/" + fileName;

        } catch (Exception e) {
            return "Error al guardar imagen: " + e.getMessage();
        }
    }
}