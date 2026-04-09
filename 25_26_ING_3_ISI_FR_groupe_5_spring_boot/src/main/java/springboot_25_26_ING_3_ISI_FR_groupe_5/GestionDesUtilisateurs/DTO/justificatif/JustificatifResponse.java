package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.justificatif;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeJustificatif;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JustificatifResponse {

    private Long id;
    private String contenu;
    private String fichierUrl;
    private LocalDateTime dateDebutAbsence;
    private LocalDateTime dateFinAbsence;
    private Long nombreHeures;
    private StatutJustificatif status;
    private String commentaireValidation;
    private LocalDateTime dateSoumission;
    private LocalDateTime dateValidation;
    private TypeJustificatif type;

    // Étudiant
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantEmail;
    private String etudiantMatricule;

    // Assistant pédagogique (optionnel)
    private Long assistantPedagogiqueId;
    private String assistantPedagogiqueNom;
    private String assistantPedagogiquePrenom;

    // Validateur (optionnel)
    private Long validateurId;
    private String validateurNom;
    private String validateurPrenom;
    private String validateurEmail;
}