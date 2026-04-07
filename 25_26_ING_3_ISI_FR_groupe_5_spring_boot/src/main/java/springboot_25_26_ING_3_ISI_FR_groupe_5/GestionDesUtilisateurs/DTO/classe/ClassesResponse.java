package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

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