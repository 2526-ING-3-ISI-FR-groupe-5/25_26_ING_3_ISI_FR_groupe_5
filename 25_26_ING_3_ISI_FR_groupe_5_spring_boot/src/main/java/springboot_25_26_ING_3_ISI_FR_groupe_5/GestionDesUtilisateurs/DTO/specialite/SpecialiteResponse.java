package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.specialite;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialiteResponse {

    private Long id;
    private String nom;
    private String code;
    private String description;

    private Long filiereId;
    private String filiereNom;
    // Ajoute ce champ
    private int nombreNiveaux;

}