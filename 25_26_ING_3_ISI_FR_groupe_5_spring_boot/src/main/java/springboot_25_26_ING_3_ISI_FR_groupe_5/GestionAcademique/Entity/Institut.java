package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.HashSet;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
// ✅ Contrainte unicité nom
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_institut_nom",
                columnNames = {"nom"}
        )
)
// ✅ Nécessaire pour Auditable
@EntityListeners(AuditingEntityListener.class)
public class Institut extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Attributs propres
    // ============================================

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String ville;

    private String adresse;
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

    // ✅ Set au lieu de Collection — cohérence
    @OneToMany(mappedBy = "institut",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private Set<Ecole> ecoles = new HashSet<>();

    /*
     * ❌ SUPPRIMÉ — utilisateurs
     * La relation Institut est portée par Utilisateur
     * (Utilisateur.institut → @ManyToOne Institut)
     * Pas besoin de l'inverse ici
     */

    // ============================================
    // Helpers — navigation
    // ============================================

    public int getNombreEcoles() {
        return ecoles != null ? ecoles.size() : 0;
    }

    // ✅ Nombre total de filières dans l'institut
    public long getNombreFilieres() {
        if (ecoles == null) return 0;
        return ecoles.stream()
                .mapToLong(e -> e.getFilieres() != null
                        ? e.getFilieres().size() : 0)
                .sum();
    }

    // ✅ Helpers ajout/suppression
    public void addEcole(Ecole ecole) {
        ecoles.add(ecole);
        ecole.setInstitut(this);
    }

    public void removeEcole(Ecole ecole) {
        ecoles.remove(ecole);
        ecole.setInstitut(null);
    }
}