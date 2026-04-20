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
@Table("google_translate")
public class GoogleTranslate {
    @Id
    private Long id;
    private String textOriginal;
    private String languageFrom;
    private String languageTo;
    private String textTranslated;
    private LocalDateTime createdAt;
}
