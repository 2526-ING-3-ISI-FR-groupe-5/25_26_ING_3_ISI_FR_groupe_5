package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.surveillant;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SurveillantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String secteur;
    private String typeContrat;
    private boolean active;
}
