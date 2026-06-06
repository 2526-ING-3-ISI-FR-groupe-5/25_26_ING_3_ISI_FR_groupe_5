package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.PresenceConstants;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.MethodeValidation;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSession;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_appel")
public class SessionAppel {

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
    @JoinColumn(name = "enseignant_id", nullable = false)
    private Enseignant enseignant;

    // ══════════════════════════════════════════
    // CODE D'ACCÈS
    // ══════════════════════════════════════════

    @Enumerated(EnumType.STRING)
    private MethodeValidation methode;

    @Column(nullable = false)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String qrCodeBase64;

    private LocalDateTime dateGeneration;
    private LocalDateTime dateExpiration;

    // ══════════════════════════════════════════
    // TYPE DE SESSION
    // ══════════════════════════════════════════

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private TypeSession typeSession = TypeSession.NORMALE;

    // ══════════════════════════════════════════
    // ÉTAT
    // ══════════════════════════════════════════

    @Builder.Default
    private boolean actif = true;

    @Builder.Default
    private boolean coursTermine = false;

    private LocalDateTime heureFinReelle;

    // ══════════════════════════════════════════
    // GÉOLOCALISATION ENSEIGNANT
    // ══════════════════════════════════════════

    private Double latitudeEnseignant;
    private Double longitudeEnseignant;
    private Integer perimetreMetres;

    // ══════════════════════════════════════════
    // APPELS
    // ══════════════════════════════════════════

    @OneToMany(mappedBy = "sessionAppel")
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS — VALIDITÉ
    // ══════════════════════════════════════════

    public boolean isValide() {
        if (!actif || coursTermine) return false;
        if (typeSession == TypeSession.OFFLINE) return true;
        return !isExpire();
    }

    public boolean isExpire() {
        return dateExpiration != null && LocalDateTime.now().isAfter(dateExpiration);
    }

    public boolean isOffline() {
        return typeSession == TypeSession.OFFLINE;
    }

    // ══════════════════════════════════════════
    // HELPERS — QR CODE
    // ══════════════════════════════════════════

    public boolean hasQrCode() {
        return qrCodeBase64 != null && !qrCodeBase64.isEmpty();
    }

    /**
     * Construit l'URL encodee dans le QR Code.
     */
    public String getQrCodeUrl(String baseUrl) {
        return baseUrl + "/etudiant/valider-presence?session=" + this.id + "&pin=" + this.code;
    }

    // ══════════════════════════════════════════
    // HELPERS — RETARD (premier cours du matin)
    // ══════════════════════════════════════════

    public boolean estPremierCoursDuMatin() {
        if (plageHoraire == null || plageHoraire.getHeureDebut() == null) return false;
        return !plageHoraire.getHeureDebut().isAfter(PresenceConstants.SEUIL_PREMIER_COURS);
    }

    // ══════════════════════════════════════════
    // HELPERS — GÉOLOCALISATION
    // ══════════════════════════════════════════

    public double calculerDistance(Double lat2, Double lon2) {
        if (latitudeEnseignant == null || longitudeEnseignant == null
                || lat2 == null || lon2 == null) return Double.MAX_VALUE;

        final int R = 6371000;
        double dLat = Math.toRadians(lat2 - latitudeEnseignant);
        double dLon = Math.toRadians(lon2 - longitudeEnseignant);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(latitudeEnseignant))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public boolean estDansLePerimetre(Double lat, Double lon) {
        if (perimetreMetres == null) return true;
        return calculerDistance(lat, lon) <= perimetreMetres;
    }

    // ══════════════════════════════════════════
    // HELPERS — STATISTIQUES
    // ══════════════════════════════════════════

    public int getNbPresents() {
        return (int) appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.PRESENT)
                .count();
    }

    public int getNbAbsents() {
        return (int) appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.ABSENT)
                .count();
    }

    public int getNbPartiels() {
        return (int) appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.PARTIEL)
                .count();
    }

    public int getNbRetards() {
        return (int) appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.RETARD)
                .count();
    }

    public int getNbEnAttente() {
        return (int) appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.EN_ATTENTE)
                .count();
    }

    public int getTotalEtudiants() {
        return appels.size();
    }

    public double getTauxPresence() {
        if (getTotalEtudiants() == 0) return 0;
        int presents = getNbPresents() + getNbRetards() + getNbPartiels();
        return (double) presents / getTotalEtudiants() * 100;
    }

    public double getRetardMoyenMinutes() {
        return appels.stream()
                .filter(a -> a.getStatut() == StatutPresence.RETARD && a.getHeureArrivee() != null)
                .mapToInt(a -> {
                    if (plageHoraire != null && plageHoraire.getHeureDebut() != null) {
                        return (int) java.time.Duration.between(
                                plageHoraire.getHeureDebut(), a.getHeureArrivee()).toMinutes();
                    }
                    return 0;
                })
                .average()
                .orElse(0);
    }

    public boolean isAppelComplet() {
        return appels.stream().noneMatch(a -> a.getStatut() == StatutPresence.EN_ATTENTE);
    }
}