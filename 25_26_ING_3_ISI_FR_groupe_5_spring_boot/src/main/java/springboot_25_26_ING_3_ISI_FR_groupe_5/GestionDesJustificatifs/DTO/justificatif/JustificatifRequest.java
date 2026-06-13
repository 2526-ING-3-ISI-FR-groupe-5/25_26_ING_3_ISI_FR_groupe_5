package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.DTO.justificatif;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.TypeJustificatif;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class JustificatifRequest {

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private String fichierUrl;

    // Ces dates deviennent optionnelles si l'étudiant sélectionne des absences (appelIds)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDebutAbsence;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateFinAbsence;

    private Long nombreHeures;

    private StatutJustificatif status;

    private String commentaireValidation;

    @NotNull(message = "Le type de justificatif est obligatoire")
    private TypeJustificatif type;

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    private Long assistantPedagogiqueId;

    private Long validateurId;

    private List<Long> appelIds;
}