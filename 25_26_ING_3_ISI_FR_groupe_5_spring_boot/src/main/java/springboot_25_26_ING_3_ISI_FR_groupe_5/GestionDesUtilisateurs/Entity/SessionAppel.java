package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutPresence;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SessionAppel extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ══════════════════════════════════════════
    // RELATIONS
    // ══════════════════════════════════════════

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plage_horaire_id", nullable = false)
    private PlageHoraire plageHoraire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enseignant_id")
    private Enseignant enseignant;

    // ══════════════════════════════════════════
    // MÉTHODE ET CODE
    // ══════════════════════════════════════════

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MethodeValidation methode;

    @Column(length = 500)
    private String code;

    // ══════════════════════════════════════════
    // VALIDITÉ TEMPORELLE
    // ══════════════════════════════════════════

    private LocalDateTime dateGeneration;
    private LocalDateTime dateExpiration;

    @Builder.Default
    private boolean actif = true;

    // ══════════════════════════════════════════
    // GÉOLOCALISATION
    // ══════════════════════════════════════════

    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;

    // ══════════════════════════════════════════
    // FIN DE COURS
    // ══════════════════════════════════════════

    private LocalDateTime heureFinReelle;

    @Builder.Default
    private boolean coursTermine = false;

    // ══════════════════════════════════════════
    // APPELS
    // ══════════════════════════════════════════

    @OneToMany(mappedBy = "sessionAppel")
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS — validité session
    // ══════════════════════════════════════════

    public boolean isExpire() {
        return dateExpiration != null && LocalDateTime.now().isAfter(dateExpiration);
    }

    public boolean isValide() {
        return actif && !isExpire() && !coursTermine;
    }

    /**
     * Indique si cette session concerne le premier cours du matin.
     * Utilisé pour autoriser ou non la saisie de retards.
     */
    public boolean estPremierCoursDuMatin() {
        if (plageHoraire == null) return false;
        return !plageHoraire.getHeureDebut().isAfter(Appels.SEUIL_PREMIER_COURS);
    }

    // ══════════════════════════════════════════
    // HELPERS — géolocalisation
    // ══════════════════════════════════════════

    public boolean estDansLePerimetre(double latEtudiant, double lonEtudiant) {
        if (latitudeEnseignant == null || longitudeEnseignant == null || perimetreMetres == null) {
            return true;
        }
        return calculerDistanceMetres(latitudeEnseignant, longitudeEnseignant,
                latEtudiant, lonEtudiant) <= perimetreMetres;
    }

    private double calculerDistanceMetres(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // ══════════════════════════════════════════
    // HELPERS — statistiques de la session
    // ══════════════════════════════════════════

    public long getNbPresents() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.PRESENT).count();
    }

    public long getNbAbsents() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.ABSENT).count();
    }

    public long getNbRetards() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.RETARD).count();
    }

    public long getNbPartiels() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.PARTIEL).count();
    }

    public long getNbEnAttente() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.EN_ATTENTE).count();
    }

    public long getNbJustifies() {
        return appels.stream().filter(a -> a.getStatut() == StatutPresence.JUSTIFIE).count();
    }

    /**
     * Taux de présence en % (présents + retards comptent comme présents).
     * Retourne 0 si aucun appel.
     */
    public double getTauxPresence() {
        if (appels.isEmpty()) return 0.0;
        long presentsEtRetards = appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.PRESENT
                        || a.getStatut() == StatutPresence.RETARD
                        || a.getStatut() == StatutPresence.PARTIEL)
                .count();
        return (presentsEtRetards * 100.0) / appels.size();
    }

    /**
     * Appels dont le statut est encore EN_ATTENTE (appel non complété).
     */
    public Set<Appels> getAppelsEnAttente() {
        return appels.stream()
                .filter(Appels::isEnAttente)
                .collect(Collectors.toSet());
    }

    /**
     * Retarde récupérables : étudiants en retard sur le premier cours du matin.
     */
    public Set<Appels> getRetards() {
        return appels.stream()
                .filter(Appels::isRetard)
                .collect(Collectors.toSet());
    }

    /**
     * Retard moyen en minutes pour cette session.
     * Retourne 0 si aucun retard enregistré.
     */
    public double getRetardMoyenMinutes() {
        return appels.stream()
                .filter(Appels::isRetard)
                .mapToInt(Appels::getRetardMinutes)
                .average()
                .orElse(0.0);
    }

    /**
     * Vérifie que tous les étudiants ont été traités (plus d'EN_ATTENTE).
     */
    public boolean isAppelComplet() {
        return appels.stream().noneMatch(Appels::isEnAttente);
    }
}