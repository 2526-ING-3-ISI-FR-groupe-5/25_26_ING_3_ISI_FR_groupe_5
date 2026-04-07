package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import CarnetRouge.CarnetRouge.GDU.Enum.StatutInscription;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionResponse {

    private Long id;
    private LocalDate dateInscription;

    // Infos étudiant
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;
    private String etudiantEmail;

    // Infos classe
    private Long classeId;
    private String classeNom;

    // Infos année académique
    private Long anneeAcademiqueId;
    private String anneeAcademiqueNom;

    // Statut et décision
    private StatutInscription statut;
    private String decisionFinAnnee;
}