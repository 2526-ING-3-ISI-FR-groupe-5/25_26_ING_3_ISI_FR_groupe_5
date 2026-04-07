package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classes;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassesResponse {

    private Long id;
    private String nom;

    // Infos Niveau
    private Long niveauId;
    private String niveauNom;
    private Integer niveauOrdre;

    // ✅ Spécialité via le niveau
    private Long specialiteId;
    private String specialiteNom;
    private String specialiteCode;
}