package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classe;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassesResponse {
    private Long id;
    private String nom;
    private Long niveauId;
    private String niveauNom;
    private Integer niveauOrdre;
    private Long specialiteId;
    private String specialiteNom;
    private String specialiteCode;
}