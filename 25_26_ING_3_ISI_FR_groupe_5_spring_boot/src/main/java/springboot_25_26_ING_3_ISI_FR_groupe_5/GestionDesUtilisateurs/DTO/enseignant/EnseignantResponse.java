package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeEnseignant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnseignantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String grade;
    private String typeEnseignant;
    private boolean active;
}
