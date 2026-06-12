package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ue.UESimpleResponse;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnseignantAvecUEResponse {
    private Long id;
    private String nom;
    private String prenom;
    private String couleur;
    private Long institutId;
    private String institutNom;
    private List<UESimpleResponse> ues;
}