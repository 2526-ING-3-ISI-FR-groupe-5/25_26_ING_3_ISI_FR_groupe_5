package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.programmation;

import lombok.*;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammationResponse {

    private Long id;

    // UE
    private Long ueId;
    private String ueNom;
    private String ueCode;
    private String ueLibelle;

    // Semestre
    private Long semestreId;
    private String semestreNom;

    // Classe
    private Long classeId;
    private String classeNom;

    // 🆕 Institut
    private Long institutId;
    private String institutNom;

    // Détails
    private Long dheure;
    private Long nbrCredit;
    private String libelle;
    private String libelleAnglais;

    // Enseignants
    private List<String> enseignantsNoms;  // Format: "Prénom Nom"
    private int nombreEnseignants;

    // 🆕 Nombre de plages horaires
    private int nombrePlagesHoraires;
}