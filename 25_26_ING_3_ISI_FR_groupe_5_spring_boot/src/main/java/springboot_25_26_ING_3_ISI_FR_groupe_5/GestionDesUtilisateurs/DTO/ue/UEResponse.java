package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ue;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UEResponse {

    private Long id;
    private String nom;
    private String code;
    private String libelle;
    private String libelleAnglais;

    private Long specialiteId;
    private String specialiteNom;
}