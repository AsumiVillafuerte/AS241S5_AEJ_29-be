package pe.edu.vallegrande.ApiAI.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("youtube_mp3")
public class YoutubeMp3 {
    @Id
    private Long id;
    private String videoUrl;
    private String downloadUrl;
    private String status;
    private LocalDateTime createdAt;
}
