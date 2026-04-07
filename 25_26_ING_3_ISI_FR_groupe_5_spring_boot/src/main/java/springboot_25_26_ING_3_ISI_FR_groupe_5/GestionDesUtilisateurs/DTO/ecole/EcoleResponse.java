package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EcoleResponse {

    private Long id;
    private String nom;
    private String adresse;
    private String email;
    private String telephone;
}