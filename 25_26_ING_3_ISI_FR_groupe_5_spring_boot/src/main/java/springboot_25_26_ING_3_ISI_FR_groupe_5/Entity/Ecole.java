package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// ✅ Contrainte unicité nom par institut
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_ecole_nom_institut",
                columnNames = {"nom", "institut_id"}
        )
)
// ✅ Nécessaire pour Auditable
@EntityListeners(AuditingEntityListener.class)
public class Ecole extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Attributs propres
    // ============================================

    @Column(nullable = false)
    private String nom;

    private String adresse;
    private String ville;
    private String localite;
    private String email;
    private String telephone;

    // ✅ Statut actif/inactif
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // ============================================
    // Relations
    // ============================================

    // ✅ FetchType.LAZY ajouté
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institut_id", nullable = false)
    private Institut institut;

    @OneToMany(mappedBy = "ecole", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Filiere> filieres = new HashSet<>();

    // ============================================
    // Helpers
    // ============================================

    public Long getInstitutId() {
        return institut != null ? institut.getId() : null;
    }

    public String getInstitutNom() {
        return institut != null ? institut.getNom() : null;
    }

    public int getNombreFilieres() {
        return filieres != null ? filieres.size() : 0;
    }

    public void addFiliere(Filiere filiere) {
        filieres.add(filiere);
        filiere.setEcole(this);
    }

    public void removeFiliere(Filiere filiere) {
        filieres.remove(filiere);
        filiere.setEcole(null);
    }
}