package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Semestre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeSemestre typeSemestre;

    private boolean actif;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private Annee_academique anneeAcademique;

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();

    @OneToMany(mappedBy = "semestre", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlageHoraire> plagesHoraires = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS POUR PROGRAMMATIONUE
    // ══════════════════════════════════════════

    public void addProgrammation(ProgrammationUE programmation) {
        programmations.add(programmation);
        programmation.setSemestre(this);
    }

    public void removeProgrammation(ProgrammationUE programmation) {
        programmations.remove(programmation);
        programmation.setSemestre(null);
    }

    // ══════════════════════════════════════════
    // HELPERS POUR PLAGEHORAIRE
    // ══════════════════════════════════════════

    public void addPlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.add(plageHoraire);
        plageHoraire.setSemestre(this);
    }

    public void removePlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.remove(plageHoraire);
        plageHoraire.setSemestre(null);
    }

    // ══════════════════════════════════════════
    // HELPERS DE VÉRIFICATION
    // ══════════════════════════════════════════

    public boolean isDansPeriode(LocalDate date) {
        return (date.isEqual(dateDebut) || date.isAfter(dateDebut)) &&
                (date.isEqual(dateFin) || date.isBefore(dateFin));
    }

    public String getLibelle() {
        return typeSemestre != null ? typeSemestre.getLibelle() : "";
    }

    public int getNumero() {
        return typeSemestre != null ? typeSemestre.getNumero() : 0;
    }
}