package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.sessionAppel;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.MethodeValidation;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SessionAppelResponse {

    private Long id;
    private MethodeValidation methode;
    private String code;
    private LocalDateTime dateGeneration;
    private LocalDateTime dateExpiration;
    private boolean actif;
    private boolean expire;
    private boolean coursTermine;

    // Géolocalisation
    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;

    // Plage horaire
    private Long plageHoraireId;
    private String plageHoraireTitre;
    private String plageHoraireJour;
    private String plageHoraireHeures;
    private Long nbHeureTotal;

    // Enseignant
    private Long enseignantId;
    private String enseignantNom;
    private String enseignantPrenom;

    // Fin de cours
    private LocalDateTime heureFinReelle;

    // Stats
    private int nbPresents;
    private int nbAbsents;
    private int nbPartiels;
    private int totalEtudiants;

    // 🆕 Stats retards
    private int nbRetards;
    private double retardMoyenMinutes;

    /**
     * Indique si ce cours autorise la saisie de retards
     * (premier cours du matin, heureDebut ≤ 08h30).
     */
    private boolean retardAutorise;

    /** Appel complet = plus aucun étudiant EN_ATTENTE. */
    private boolean appelComplet;

    /** Taux de présence en % (présents + retards + partiels). */
    private double tauxPresence;
}