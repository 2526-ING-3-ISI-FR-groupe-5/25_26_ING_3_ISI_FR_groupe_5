package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutResponse {

    private Long id;
    private String nom;
    private String ville;
    private String adresse;
    private String email;
    private String telephone;
    private String localite;
    private int nombreEcoles;
    private int nombreUtilisateurs;
}