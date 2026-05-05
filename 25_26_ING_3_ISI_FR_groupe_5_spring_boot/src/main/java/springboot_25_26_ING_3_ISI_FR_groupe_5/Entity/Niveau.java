package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// ✅ Contrainte unicité code par filière
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_niveau_code_filiere",
                columnNames = {"code", "filiere_id"}
        )
)
// ✅ Nécessaire pour Auditable
@EntityListeners(AuditingEntityListener.class)
public class Niveau extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Attributs propres
    // ============================================

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String code;

    // ✅ Ordre pour trier les niveaux (L1=1, L2=2, L3=3, M1=4, M2=5)
    @Column(nullable = false)
    private Integer ordre;

    // ✅ Statut actif/inactif
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // ============================================
    // Relations
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialite_id")
    private Specialite specialite;

    @OneToMany(mappedBy = "niveau")
    @Builder.Default
    private Set<Classe> classes = new HashSet<>();

    // ============================================
    // Helpers — navigation
    // ============================================

    // ✅ Accès à l'école via filière
    public Long getEcoleId() {
        return filiere != null && filiere.getEcole() != null
                ? filiere.getEcole().getId()
                : null;
    }

    // ✅ Accès à l'institut via filière → école
    public Long getInstitutId() {
        return filiere != null
                ? filiere.getInstitutId()
                : null;
    }

    // ✅ Niveau supérieur — utilisé dans MigrationService
    public boolean hasNiveauSuperieur() {
        if (filiere == null) return false;
        return filiere.getNiveaux().stream()
                .anyMatch(n -> n.getOrdre() != null
                        && this.ordre != null
                        && n.getOrdre() > this.ordre);
    }
}