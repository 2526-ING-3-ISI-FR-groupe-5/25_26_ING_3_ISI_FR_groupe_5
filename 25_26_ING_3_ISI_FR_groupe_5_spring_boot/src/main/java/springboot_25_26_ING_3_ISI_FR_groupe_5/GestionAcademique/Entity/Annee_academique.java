package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_annee_nom_institut",
                columnNames = {"nom", "institut_id"}
        )
})
public class Annee_academique extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String nom;                    // ex: "2025-2026"

    private boolean active = false;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    // 🆕 Rattachement à l'institut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institut_id", nullable = false)
    private Institut institut;

    @OneToMany(mappedBy = "anneeAcademique", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Semestre> semestres = new HashSet<>();

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════

    public void addSemestre(Semestre semestre) {
        semestres.add(semestre);
        semestre.setAnneeAcademique(this);
    }

    public void removeSemestre(Semestre semestre) {
        semestres.remove(semestre);
        semestre.setAnneeAcademique(null);
    }

    // Vérifier si une année est active pour un institut donné
    public boolean isActivePourInstitut(Long institutId) {
        return active && institut != null && institut.getId().equals(institutId);
    }
}