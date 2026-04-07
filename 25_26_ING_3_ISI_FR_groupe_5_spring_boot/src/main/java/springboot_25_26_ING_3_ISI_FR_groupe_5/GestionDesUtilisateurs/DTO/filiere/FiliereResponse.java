package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiliereResponse {

    private Long id;
    private String nom;
    private String code;
    private String description;

    private Long ecoleId;
    private String ecoleNom;

    private Long cycleId;
    private String cycleNom;
    private String cycleLibelle;
}