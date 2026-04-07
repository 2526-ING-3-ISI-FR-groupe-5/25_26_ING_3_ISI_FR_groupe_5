package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SemestreResponse {

    private Long id;
    private TypeSemestre typeSemestre;
    private String libelle;
    private Integer numero;
    private boolean actif;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    private Long anneeAcademiqueId;
    private String anneeAcademiqueNom;
}