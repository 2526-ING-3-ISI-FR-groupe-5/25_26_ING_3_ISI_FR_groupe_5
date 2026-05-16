package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationBatchStatus;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "migration_batch", indexes = {
        @Index(name = "idx_batch_institut_annee", columnList = "institut_id, source_annee_id, target_annee_id"),
        @Index(name = "idx_batch_status", columnList = "status")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MigrationBatch extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institut_id", nullable = false)
    private Institut institut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_annee_id", nullable = false)
    private Annee_academique sourceAnnee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_annee_id", nullable = false)
    private Annee_academique targetAnnee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationBatchStatus status;

    @Enumerated(EnumType.STRING)
    private TypeMigration typeMigration;

    @Builder.Default
    private boolean rollbackPossible = true;

    private LocalDateTime dateExecution;
    private LocalDateTime datePublication;

    @Column(length = 500)
    private String motif;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MigrationDecision> decisions = new HashSet<>();

    /**
     * Le rollback est possible uniquement si :
     * - statut = TERMINEE (pas encore publié, pas déjà annulé)
     * - rollbackPossible = true
     */
    public boolean peutEtreRollback() {
        return status == MigrationBatchStatus.TERMINEE && rollbackPossible;
    }

    /**
     * La publication est possible uniquement si :
     * - statut = TERMINEE
     * - rollbackPossible = true (pas encore publié)
     */
    public boolean peutEtrePublie() {
        return status == MigrationBatchStatus.TERMINEE && rollbackPossible;
    }

    /**
     * TERMINEE → PUBLIEE : migration définitive, rollback impossible.
     */
    public void validerPublication() {
        this.datePublication = LocalDateTime.now();
        this.rollbackPossible = false;
        this.status = MigrationBatchStatus.PUBLIEE;
    }
}