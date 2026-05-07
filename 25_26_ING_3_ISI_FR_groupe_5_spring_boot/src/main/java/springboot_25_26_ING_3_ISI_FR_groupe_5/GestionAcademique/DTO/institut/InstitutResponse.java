package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.institut;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

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