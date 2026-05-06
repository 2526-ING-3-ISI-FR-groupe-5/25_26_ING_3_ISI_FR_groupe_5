package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
// ✅ Contrainte unicité code par filière
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_specialite_code_filiere",
                columnNames = {"code", "filiere_id"}
        )
)
// ✅ Nécessaire pour que Auditable fonctionne
@EntityListeners(AuditingEntityListener.class)
public class Specialite extends Auditable {

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
    @JoinColumn(name = "filiere_id", nullable = false)
    private Filiere filiere;

    @OneToMany(mappedBy = "specialite", fetch = FetchType.LAZY)
    @Builder.Default
    private Collection<Niveau> niveaux = new ArrayList<>();

    // ============================================
    // Helpers — navigation multi-instituts
    // ============================================

    // ✅ Accès rapide à l'école via Filière
    public Long getEcoleId() {
        return filiere != null && filiere.getEcole() != null
                ? filiere.getEcole().getId()
                : null;
    }

    // ✅ Accès rapide à l'institut via Filière → École
    public Long getInstitutId() {
        return filiere != null
                ? filiere.getInstitutId()
                : null;
    }

    public String getInstitutNom() {
        return filiere != null
                ? filiere.getInstitutNom()
                : null;
    }
}