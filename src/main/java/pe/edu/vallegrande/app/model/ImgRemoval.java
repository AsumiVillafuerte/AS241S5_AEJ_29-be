package pe.edu.vallegrande.app.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "img_removal")
public class ImgRemoval {

    @Id
    @Column(value = "id_removal")
    private Long idRemoval;

    @Column(value = "image_url")
    private String imageUrl;

    @Column(value = "result_url")
    private String resultUrl;

    @Column(value = "api_used")
    private String apiUsed;

    @Column(value = "response_raw")
    private String responseRaw;

    @Column(value = "status")
    private String status = "A";

    @Column(value = "created_at")
    private LocalDateTime createdAt;
}