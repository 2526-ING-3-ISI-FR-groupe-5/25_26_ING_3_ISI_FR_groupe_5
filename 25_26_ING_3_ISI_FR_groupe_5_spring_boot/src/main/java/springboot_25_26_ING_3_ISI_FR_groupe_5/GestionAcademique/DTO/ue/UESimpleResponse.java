package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UESimpleResponse {
    private Long id;
    private String nom;
    private String code;
    private String semestre;
    private String annee;
    private Long institutId;
    private String institutNom;
}