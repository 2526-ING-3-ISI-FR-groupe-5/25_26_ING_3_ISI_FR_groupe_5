package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.annee;

import lombok.*;

import java.time.LocalDate;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnneeResponse {

    private Long id;
    private String nom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private boolean active;

    // 🆕 Informations sur l'institut
    private Long institutId;
    private String institutNom;
    private String institutVille;
}