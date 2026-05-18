package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.time.LocalTime;
import java.time.LocalDateTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_appels_etudiant_plage",
                columnNames = {"etudiant_id", "plage_horaire_id"}
        )
)
public class Appels {

    /**
     * Seuil heure de début pour considérer un cours comme "premier cours du matin".
     * Un retard n'est applicable que si heureDebut <= ce seuil.
     */
    public static final LocalTime SEUIL_PREMIER_COURS = LocalTime.of(8, 30);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ══════════════════════════════════════════
    // PRÉSENCE
    // ══════════════════════════════════════════

    @Column(nullable = false)
    @Builder.Default
    private boolean present = false;

    @Column(nullable = false)
    @Builder.Default
    private int nbHeuresPresent = 0;

    // ══════════════════════════════════════════
    // RETARD
    // ══════════════════════════════════════════

    /**
     * Heure d'arrivée réelle de l'étudiant.
     * Renseignée par l'enseignant uniquement pour les retards
     * sur le premier cours du matin (heureDebut ≤ 08:30).
     */
    private LocalTime heureArrivee;

    // ══════════════════════════════════════════
    // STATUT
    // ══════════════════════════════════════════

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private StatutPresence statut = StatutPresence.EN_ATTENTE;

    @Column(length = 500)
    private String commentaire;

    // ══════════════════════════════════════════
    // MÉTHODE DE VALIDATION
    // ══════════════════════════════════════════

    @Enumerated(EnumType.STRING)
    private MethodeValidation methode;

    private String codeUtilise;

    private LocalDateTime dateValidation;

    // ══════════════════════════════════════════
    // GÉOLOCALISATION ÉTUDIANT
    // ══════════════════════════════════════════

    private Double latitudeEtudiant;
    private Double longitudeEtudiant;
    private boolean dansLePerimetre;

    // ══════════════════════════════════════════
    // PWA — SYNCHRONISATION OFFLINE
    // ══════════════════════════════════════════

    @Builder.Default
    private boolean synchronise = true;

    private LocalDateTime dateSynchronisation;

    // ══════════════════════════════════════════
    // RELATIONS
    // ══════════════════════════════════════════

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plage_horaire_id", nullable = false)
    private PlageHoraire plageHoraire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_appel_id")
    private SessionAppel sessionAppel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "justificatif_id")
    private Justificatif justificatif;

    // ══════════════════════════════════════════
    // HELPERS — statut
    // ══════════════════════════════════════════

    public boolean isPresentToutLeCours() {
        return statut == StatutPresence.PRESENT;
    }

    public boolean isAbsentToutLeCours() {
        return statut == StatutPresence.ABSENT;
    }

    public boolean isRetard() {
        return statut == StatutPresence.RETARD;
    }

    public boolean isPartiel() {
        return statut == StatutPresence.PARTIEL;
    }

    public boolean isAbsenceJustifiee() {
        return statut == StatutPresence.JUSTIFIE && justificatif != null;
    }

    public boolean isEnAttente() {
        return statut == StatutPresence.EN_ATTENTE;
    }

    public String getStatutLibelle() {
        return switch (statut) {
            case EN_ATTENTE -> "En attente";
            case PRESENT    -> "Présent";
            case RETARD     -> "En retard" + (getRetardMinutes() > 0 ? " (" + getRetardMinutes() + " min)" : "");
            case PARTIEL    -> "Partiel";
            case ABSENT     -> "Absent";
            case JUSTIFIE   -> "Justifié";
        };
    }

    // ══════════════════════════════════════════
    // HELPERS — retard
    // ══════════════════════════════════════════

    /**
     * Indique si un retard peut être marqué pour cette séance.
     * Règle : uniquement si c'est le premier cours du matin (heureDebut ≤ 08:30).
     */
    public boolean isRetardAutorise() {
        if (plageHoraire == null) return false;
        return !plageHoraire.getHeureDebut().isAfter(SEUIL_PREMIER_COURS);
    }

    /**
     * Nombre de minutes de retard par rapport à l'heure de début du cours.
     * Retourne 0 si heureArrivee non renseignée ou si pas de retard.
     */
    public int getRetardMinutes() {
        if (heureArrivee == null || plageHoraire == null || statut != StatutPresence.RETARD) return 0;
        int minutes = (int) java.time.Duration.between(plageHoraire.getHeureDebut(), heureArrivee).toMinutes();
        return Math.max(0, minutes);
    }

    /**
     * Marque l'étudiant en retard.
     * Lève une exception si la règle métier n'est pas respectée.
     */
    public void marquerRetard(LocalTime heureArrivee, Enseignant enseignant) {
        if (!isRetardAutorise()) {
            throw new IllegalStateException(
                    "Le retard n'est applicable qu'au premier cours du matin (début ≤ 08h30). " +
                            "Ce cours commence à " + plageHoraire.getHeureDebut() + "."
            );
        }
        if (heureArrivee != null && plageHoraire != null
                && heureArrivee.isBefore(plageHoraire.getHeureDebut())) {
            throw new IllegalArgumentException(
                    "L'heure d'arrivée (" + heureArrivee + ") ne peut pas être avant l'heure de début du cours."
            );
        }
        this.statut         = StatutPresence.RETARD;
        this.heureArrivee   = heureArrivee;
        this.enseignant     = enseignant;
        this.dateValidation = LocalDateTime.now();
        this.methode        = MethodeValidation.MANUELLE;
        this.present        = false;
        // Retard = présent pour une partie du cours — on compte les heures restantes
        if (plageHoraire != null) {
            int minutesCours = (int) plageHoraire.getDureeMinutes();
            int minutesRetard = getRetardMinutesDepuisHeure(heureArrivee);
            int minutesPresent = Math.max(0, minutesCours - minutesRetard);
            this.nbHeuresPresent = minutesPresent / 60;
        }
    }

    /**
     * ✅ CORRIGÉ — Marque l'étudiant présent et renseigne nbHeuresPresent
     * avec la durée complète du cours.
     * Avant : nbHeuresPresent restait à 0, faussant les stats de présence.
     */
    public void marquerPresent(Enseignant enseignant, MethodeValidation methode) {
        this.statut          = StatutPresence.PRESENT;
        this.present         = true;
        this.heureArrivee    = null;
        this.enseignant      = enseignant;
        this.dateValidation  = LocalDateTime.now();
        this.methode         = methode;
        // ✅ CORRIGÉ — on renseigne les heures de présence effectives
        this.nbHeuresPresent = plageHoraire != null ? (int) plageHoraire.getDureeHeures() : 0;
    }

    /**
     * Marque l'étudiant absent.
     */
    public void marquerAbsent(Enseignant enseignant) {
        this.statut          = StatutPresence.ABSENT;
        this.present         = false;
        this.heureArrivee    = null;
        this.nbHeuresPresent = 0;
        this.enseignant      = enseignant;
        this.dateValidation  = LocalDateTime.now();
        this.methode         = MethodeValidation.MANUELLE;
    }

    /**
     * Marque l'étudiant partiellement présent.
     *
     * @param nbHeures nombre d'heures de présence effective
     */
    public void marquerPartiel(int nbHeures, Enseignant enseignant) {
        this.statut          = StatutPresence.PARTIEL;
        this.present         = false;
        this.nbHeuresPresent = nbHeures;
        this.enseignant      = enseignant;
        this.dateValidation  = LocalDateTime.now();
        this.methode         = MethodeValidation.MANUELLE;
    }

    // ══════════════════════════════════════════
    // HELPERS — méthode de validation
    // ══════════════════════════════════════════

    public boolean isValideParQR() {
        return methode == MethodeValidation.QR_CODE;
    }

    public boolean isValideParPIN() {
        return methode == MethodeValidation.CODE_PIN;
    }

    public boolean isValideManuellement() {
        return methode == MethodeValidation.MANUELLE;
    }

    // ══════════════════════════════════════════
    // HELPERS — institut (filtrage multi-tenant)
    // ══════════════════════════════════════════

    public Long getInstitutId() {
        if (etudiant != null && etudiant.getInstitut() != null) {
            return etudiant.getInstitut().getId();
        }
        return null;
    }

    // ══════════════════════════════════════════
    // PRIVÉ
    // ══════════════════════════════════════════

    private int getRetardMinutesDepuisHeure(LocalTime heure) {
        if (heure == null || plageHoraire == null) return 0;
        return (int) java.time.Duration.between(plageHoraire.getHeureDebut(), heure).toMinutes();
    }
}