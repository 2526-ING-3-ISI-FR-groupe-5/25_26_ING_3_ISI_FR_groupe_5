package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.time.LocalDate;
import java.time.LocalDateTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InscriptionResponse {

    private Long id;
    private LocalDateTime dateInscription;

    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;
    private String etudiantEmail;

    private Long classeId;
    private String classeNom;

    private Long anneeAcademiqueId;
    private String anneeAcademiqueNom;

    // 🆕 Informations sur l'institut (remonte via la classe ou l'année)
    private Long institutId;
    private String institutNom;

    private StatutInscription statut;
    private DecisionFinAnnee decisionFinAnnee;
    private LocalDate dateDecision;
    private String observations;

    private String creePar;
    private String modifiePar;
}