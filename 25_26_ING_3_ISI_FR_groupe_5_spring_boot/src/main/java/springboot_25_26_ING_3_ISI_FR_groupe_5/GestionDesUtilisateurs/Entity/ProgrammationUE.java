package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;
// ... imports ...

import io.jsonwebtoken.lang.Classes;
import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.UE;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammationUE {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ue_id", nullable = false)
    private UE ue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    private String libelle;
    private String libelleAnglais;
    private Long nbrCredit;
    private Long dheure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classe_id", nullable = false)
    private Classe classe;

    @ManyToMany
    @JoinTable(
            name = "programmation_enseignant",
            joinColumns = @JoinColumn(name = "programmation_id"),
            inverseJoinColumns = @JoinColumn(name = "enseignant_id")
    )
    private Set<Enseignant> enseignants = new HashSet<>();

    @OneToMany(mappedBy = "programmationUE", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlageHoraire> plagesHoraires = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS POUR PLAGEHORAIRE
    // ══════════════════════════════════════════

    public void addPlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.add(plageHoraire);
        plageHoraire.setProgrammationUE(this);
    }

    public void removePlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.remove(plageHoraire);
        plageHoraire.setProgrammationUE(null);
    }

    // ══════════════════════════════════════════
    // HELPERS POUR ENSEIGNANTS
    // ══════════════════════════════════════════

    public void addEnseignant(Enseignant enseignant) {
        enseignants.add(enseignant);
        enseignant.getProgrammations().add(this);
    }

    public void removeEnseignant(Enseignant enseignant) {
        enseignants.remove(enseignant);
        enseignant.getProgrammations().remove(this);
    }

    // ══════════════════════════════════════════
    // HELPERS DE VÉRIFICATION
    // ══════════════════════════════════════════

    public boolean isDansAnneeActive(Annee_academique anneeActive) {
        return semestre != null && semestre.getAnneeAcademique().getId().equals(anneeActive.getId());
    }

    public boolean isDansSemestreActif() {
        return semestre != null && semestre.isActif();
    }

    public String getEnseignantsAsString() {
        if (enseignants == null || enseignants.isEmpty()) {
            return "Aucun enseignant";
        }
        return enseignants.stream()
                .map(e -> e.getPrenom() + " " + e.getNom())
                .collect(Collectors.joining(", "));
    }
}