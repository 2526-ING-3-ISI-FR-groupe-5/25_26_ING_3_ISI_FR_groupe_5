package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.time.LocalTime;
import java.time.LocalDateTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppelsResponse {

    private Long id;

    // Présence
    private boolean present;
    private int nbHeuresPresent;
    private int nbHeuresAbsent;
    private int totalHeures;

    // Statut
    private StatutPresence statut;
    private String statutLibelle;
    private String commentaire;

    // 🆕 Retard
    private LocalTime heureArrivee;
    private int retardMinutes;
    private boolean retardAutorise;   // indique si ce cours permet les retards

    // Méthode
    private MethodeValidation methode;
    private String codeUtilise;
    private LocalDateTime dateValidation;

    // Géolocalisation
    private Double latitudeEtudiant;
    private Double longitudeEtudiant;
    private boolean dansLePerimetre;

    // Étudiant
    private Long etudiantId;
    private String etudiantNom;
    private String etudiantPrenom;
    private String etudiantMatricule;

    // Plage horaire
    private Long plageHoraireId;
    private String plageHoraireTitre;
    private String plageHoraireJour;
    private String plageHoraireHeures;

    // Enseignant
    private Long enseignantId;
    private String enseignantNom;
    private String enseignantPrenom;

    // Session
    private Long sessionAppelId;
    private MethodeValidation sessionMethode;

    // Justificatif
    private Long justificatifId;
    private String justificatifStatut;

    // Institut
    private Long institutId;
    private String institutNom;

    // PWA
    private boolean synchronise;
    private LocalDateTime dateSynchronisation;
}