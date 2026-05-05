package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeSeance;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    // ✅ Lien avec le système d'appel
    @OneToMany(mappedBy = "plageHoraire", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<SessionAppel> sessions = new HashSet<>();

    @OneToMany(mappedBy = "plageHoraire", cascade = CascadeType.ALL)
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
}