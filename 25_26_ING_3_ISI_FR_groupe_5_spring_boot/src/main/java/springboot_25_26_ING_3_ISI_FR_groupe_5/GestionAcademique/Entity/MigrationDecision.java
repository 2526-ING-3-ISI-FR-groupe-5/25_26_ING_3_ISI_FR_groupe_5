package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationDecisionStatus;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationSourceType;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

@Entity
@Table(name = "migration_decision", indexes = {
        @Index(name = "idx_decision_batch", columnList = "batch_id"),
        @Index(name = "idx_decision_source", columnList = "source_type, source_id"),
        @Index(name = "idx_decision_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MigrationDecision extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id", nullable = false)
    private MigrationBatch batch;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MigrationSourceType sourceType;

    @Column(nullable = false)
    private Long sourceId;

    /** Référence lisible (ex: code UE, matricule, nom classe) */
    @Column(length = 100, nullable = false)
    private String sourceReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_annee_id")
    private Annee_academique targetAnnee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_semestre_id")
    private Semestre targetSemestre;

    @Enumerated(EnumType.STRING)
    private MigrationDecisionStatus status;

    /** Modifications appliquées lors du clonage (JSON) */
    @Column(columnDefinition = "TEXT")
    private String modifications;

    /** Rollback possible tant que le batch n'est pas publié */
    @Builder.Default
    private boolean rollbackPossible = true;

    // Helpers
    public void marquerSucces() { this.status = MigrationDecisionStatus.MIGREE; }
    public void marquerErreur() { this.status = MigrationDecisionStatus.ERREUR; }
}