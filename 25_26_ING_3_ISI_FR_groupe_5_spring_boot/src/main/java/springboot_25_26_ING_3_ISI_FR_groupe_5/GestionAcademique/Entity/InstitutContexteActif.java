package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDateTime;

/**
 * Pivot garantissant UN SEUL contexte académique actif par institut.
 * Permet une bascule atomique, un filtrage performant par semestre actif,
 * et un audit complet des migrations d'année/semestre.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "institut_contexte_actif",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_contexte_actif_institut",
                columnNames = "institut_id"
        ))
public class InstitutContexteActif extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 1:1 stricte avec l'Institut. La contrainte UNIQUE sur institut_id
     * empêche d'avoir deux contextes actifs pour le même institut.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institut_id", nullable = false, unique = true)
    private Institut institut;

    /** Année académique actuellement active pour cet institut */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "annee_academique_id", nullable = false)
    private Annee_academique anneeAcademique;

    /** Semestre actuellement actif dans cette année */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semestre_id", nullable = false)
    private Semestre semestre;

    /** Horodatage de la dernière bascule de contexte */
    @Column(nullable = false)
    private LocalDateTime derniereBascule;

    /** Utilisateur ayant validé la bascule (audit métier) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bascule_par_id")
    private Utilisateur basculePar;

    // ═══════════════════════════════════════════════════════════
    // HELPERS DE VALIDATION
    // ═══════════════════════════════════════════════════════════

    /** Vérifie que le semestre appartient bien à l'année enregistrée */
    public boolean isContexteCoherent() {
        return semestre != null && anneeAcademique != null
                && anneeAcademique.getSemestres().contains(semestre);
    }
}