package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor @NoArgsConstructor
public class NiveauResponse {

    private Long id;
    private String nom;
    private String code;
    private Integer ordre;

    // Filière
    private Long filiereId;
    private String filiereNom;

    // Spécialité (optionnel)
    private Long specialiteId;
    private String specialiteNom;
}