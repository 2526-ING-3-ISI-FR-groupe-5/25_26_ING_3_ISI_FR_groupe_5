package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Annee_Academique;


import lombok.*;

import java.time.LocalDate;

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
}
