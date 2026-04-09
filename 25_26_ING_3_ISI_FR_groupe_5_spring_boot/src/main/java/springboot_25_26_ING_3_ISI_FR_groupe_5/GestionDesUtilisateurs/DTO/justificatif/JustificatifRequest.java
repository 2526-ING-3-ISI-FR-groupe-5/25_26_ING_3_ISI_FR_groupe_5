package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.justificatif;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeJustificatif;

import java.time.LocalDateTime;

@Getter
@Setter
public class JustificatifRequest {

    @NotBlank(message = "Le contenu est obligatoire")
    private String contenu;

    private String fichierUrl;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDebutAbsence;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateFinAbsence;

    private Long nombreHeures;

    private StatutJustificatif status;

    private String commentaireValidation;

    private TypeJustificatif type;

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    private Long assistantPedagogiqueId;

    private Long validateurId;
}