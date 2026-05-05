package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
// ✅ Contrainte unicité code par école
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_filiere_code_ecole",
                columnNames = {"code", "ecole_id"}
        )
)
// ✅ Nécessaire pour que Auditable fonctionne
@EntityListeners(AuditingEntityListener.class)
public class Filiere extends Auditable {

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

    @Column(length = 500)
    private String description;

    // ✅ Statut actif/inactif
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // ============================================
    // Relations
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id")
    private Cycle cycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ecole_id", nullable = false)
    private Ecole ecole;

    @OneToMany(mappedBy = "filiere", fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Specialite> specialites = new ArrayList<>();

    @OneToMany(mappedBy = "filiere", fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Niveau> niveaux = new ArrayList<>();

    // ============================================
    // Helpers
    // ============================================

    // ✅ Accès rapide à l'institut via Ecole
    public Long getInstitutId() {
        return ecole != null && ecole.getInstitut() != null
                ? ecole.getInstitut().getId()
                : null;
    }

    public String getInstitutNom() {
        return ecole != null && ecole.getInstitut() != null
                ? ecole.getInstitut().getNom()
                : null;
    }


}