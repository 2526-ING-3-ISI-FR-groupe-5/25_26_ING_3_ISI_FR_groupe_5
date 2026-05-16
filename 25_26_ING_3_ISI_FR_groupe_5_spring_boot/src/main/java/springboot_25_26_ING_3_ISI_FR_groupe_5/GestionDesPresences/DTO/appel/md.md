package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Getter
@Setter
public class AppelRetardRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    @NotNull(message = "L'enseignant est obligatoire")
    private Long enseignantId;
    
    @NotNull(message = "L'heure d'arrivée est obligatoire")
    private LocalTime heureArrivee;

    private String commentaire;
}

package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Getter
@Setter
public class AppelsCheckManuelRequest {

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    @NotNull(message = "L'enseignant est obligatoire")
    private Long enseignantId;

    /** IDs des étudiants cochés "Présents". */
    private List<Long> etudiantIdsPresents;

    /** Étudiants avec présence partielle. */
    private List<PresencePartielle> presencesPartielles;

    /**
     * 🆕 Étudiants en retard — uniquement pour le premier cours du matin.
     * Le service vérifie la règle métier avant d'appliquer.
     */
    private List<Retard> retards;

    // ── Nested classes ──

    @Getter
    @Setter
    public static class PresencePartielle {
        private Long etudiantId;
        private int nbHeuresPresent;
    }

    @Getter
    @Setter
    public static class Retard {
        private Long etudiantId;

        /**
         * Heure d'arrivée réelle de l'étudiant retardataire.
         * Doit être après l'heure de début du cours.
         */
        private LocalTime heureArrivee;

        private String commentaire;
    }
}

package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.time.LocalTime;

@Getter
@Setter
public class AppelsRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    private Long enseignantId;

    // Présence
    private boolean present = false;
    private int nbHeuresPresent = 0;

    // Statut explicite (remplace present=true/false)
    private StatutPresence statut;

    /**
     * 🆕 Heure d'arrivée — renseignée uniquement pour les retards
     * (premier cours du matin, heureDebut ≤ 08h30).
     */
    private LocalTime heureArrivee;

    // Méthode
    private MethodeValidation methode;

    // Validation numérique (QR/PIN)
    private String codeSaisi;
    private Long sessionAppelId;

    // Géolocalisation étudiant
    private Double latitudeEtudiant;
    private Double longitudeEtudiant;

    private String commentaire;
}
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

package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class EnseignantUEStatsDTO {
private String ueCode;
private String ueLibelle;
private String classeNom;
private double heuresPrevues;   // ProgrammationUE.dheure
private double heuresRealisees; // Somme des durées des cours terminés
private double heuresRestantes;
private double progression;     // Pourcentage
}

package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.stats;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EtudiantStatsDTO {
private long nbPresences;
private long nbRetards;
private long nbAbsencesNJ;
private long nbAbsencesJ;
private double tauxPresence;
private double totalHeuresPresent;
}