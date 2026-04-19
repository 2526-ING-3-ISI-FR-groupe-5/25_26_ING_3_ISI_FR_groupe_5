package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EtudiantResponse {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String matricule;
    private String telephone;
    private LocalDate dateNaissance;
    private boolean active;
    private TypeNiveau niveau;
    private String classeNom;
    private String anneeNom;
    private StatutInscription statut;
    private String decisionFinAnnee;
}
