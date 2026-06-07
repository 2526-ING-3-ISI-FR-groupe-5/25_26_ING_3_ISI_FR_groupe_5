package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.TypeSeance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Evenement;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlageHoraire extends Auditable { // ✅ Auditable ajouté

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Créneau
    // ============================================

    @Column(nullable = false)
    private LocalDate jour;

    @Column(name = "jour_fin")
    private LocalDate jourFin;

    @Column(nullable = false)
    private LocalTime heureDebut;

    @Column(nullable = false)
    private LocalTime heureFin;

    // ============================================
    // Informations
    // ============================================

    private String salle;
    private String couleur;
    private String titre;

    // ✅ Enum au lieu de String
    @Enumerated(EnumType.STRING)
    @Column(name = "type_seance", nullable = false)
    @Builder.Default
    private TypeSeance typeSeance = TypeSeance.CM;

    // ============================================
    // Relations
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "programmation_ue_id")
    private ProgrammationUE programmationUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id")
    private Semestre semestre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "plage_horaire_enseignant",
            joinColumns = @JoinColumn(name = "plage_id"),
            inverseJoinColumns = @JoinColumn(name = "enseignant_id")
    )
    @Builder.Default
    private Set<Enseignant> enseignants = new HashSet<>();

    // ⚠️ Cascade volontairement limité à PERSIST + MERGE :
    //   Politique : on conserve l'historique de presence de TOUS les etudiants.
    //   Sans cascade REMOVE, supprimer une PlageHoraire qui a des SessionAppel
    //   ou des Appels associes leve une ConstraintViolationException (les FK
    //   Appels.plage_horaire_id et SessionAppel.plage_horaire_id sont nullable=false).
    //
    //   Effet de bord positif : casse aussi la chaine de suppression qui passerait
    //   par Semestre/Classe/ProgrammationUE -> PlageHoraire -> Appels, donc tous
    //   les appels historiques sont proteges meme via les cascades amont.
    //
    //   Pour vraiment supprimer une plage avec des appels, le code metier doit
    //   d'abord archiver/effacer les appels explicitement.
    @OneToMany(mappedBy = "plageHoraire", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Builder.Default
    private Set<SessionAppel> sessions = new HashSet<>();

    @OneToMany(mappedBy = "plageHoraire", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    // ============================================
    // Helpers — créneau
    // ============================================

    public LocalDate getJourFinEffectif() {
        return jourFin != null ? jourFin : jour;
    }

    public long getDureeMinutes() {
        return java.time.Duration.between(heureDebut, heureFin).toMinutes();
    }

    public long getDureeHeures() {
        return getDureeMinutes() / 60;
    }

    public boolean isMultiJours() {
        return jourFin != null && jourFin.isAfter(jour);
    }

    // ============================================
    // Helpers — type
    // ============================================

    // ✅ Utilise l'Enum
    public boolean isPause() {
        return TypeSeance.PAUSE == typeSeance;
    }

    public boolean isEvenement() {
        return TypeSeance.EVENEMENT == typeSeance;
    }

    public boolean isCours() {
        return !isPause() && !isEvenement();
    }

    // ============================================
    // Helpers — enseignants
    // ============================================

    public void addEnseignant(Enseignant enseignant) {
        this.enseignants.add(enseignant);
    }

    public void removeEnseignant(Enseignant enseignant) {
        this.enseignants.remove(enseignant);
    }

    public void setEnseignantUnique(Enseignant enseignant) {
        this.enseignants.clear();
        if (enseignant != null) {
            this.enseignants.add(enseignant);
        }
    }

    public String getEnseignantsNoms() {
        return enseignants.stream()
                .map(e -> e.getNom() + " " + e.getPrenom())
                .collect(Collectors.joining(" / "));
    }

    public Enseignant getPremierEnseignant() {
        return enseignants.isEmpty() ? null
                : enseignants.iterator().next();
    }

    // ============================================
    // Helpers — titre affiché
    // ============================================

    public String getTitreAffiche() {
        if (titre != null && !titre.isBlank()) return titre;
        if (programmationUE != null && programmationUE.getUe() != null) {
            return programmationUE.getUe().getNom();
        }
        return "Sans titre";
    }

    public String getSousTitreAffiche() {
        var parts = new java.util.ArrayList<String>();
        if (!enseignants.isEmpty()) {
            String ens = enseignants.stream()
                    .map(e -> e.getNom() + " "
                            + e.getPrenom().charAt(0) + ".")
                    .collect(Collectors.joining(", "));
            parts.add(ens);
        }
        if (salle != null && !salle.isBlank()) parts.add(salle);
        return String.join(" · ", parts);
    }

    // ============================================
    // Helpers — conflits
    // ============================================

    public boolean chevauche(
            LocalDate jour,
            LocalTime debut,
            LocalTime fin) {
        if (!this.jour.equals(jour)) return false;
        return this.heureDebut.isBefore(fin)
                && this.heureFin.isAfter(debut);
    }

    // ============================================
    // Helpers — état d'appel (pour DTO / template)
    // ============================================

    /**
     * Vrai s'il existe au moins une session d'appel actuellement
     * active et non terminée sur cette plage.
     */
    public boolean isAppelEnCours() {
        if (sessions == null || sessions.isEmpty()) return false;
        return sessions.stream()
                .anyMatch(s -> s.isActif() && !s.isCoursTermine());
    }

    /**
     * Vrai si au moins une session a été marquée comme cours terminé.
     */
    public boolean isCoursTermine() {
        if (sessions == null || sessions.isEmpty()) return false;
        return sessions.stream().anyMatch(SessionAppel::isCoursTermine);
    }
}