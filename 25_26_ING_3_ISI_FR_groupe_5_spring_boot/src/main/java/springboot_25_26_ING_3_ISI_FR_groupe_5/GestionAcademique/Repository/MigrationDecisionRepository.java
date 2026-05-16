package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.MigrationDecision;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationDecisionStatus;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationSourceType;

import java.util.List;
import java.util.Optional;

@Repository
public interface MigrationDecisionRepository extends JpaRepository<MigrationDecision, Long> {

    // ═══════════════════════════════════════════════════════════
    // GESTION DES DÉCISIONS DANS UN BATCH
    // ═══════════════════════════════════════════════════════════

    // Liste brute des éléments sélectionnés
    List<MigrationDecision> findByBatchId(Long batchId);

    // Filtrer par résultat (ex: pour voir les erreurs ou les réussites)
    List<MigrationDecision> findByBatchIdAndStatus(Long batchId, MigrationDecisionStatus status);

    // ═══════════════════════════════════════════════════════════
    // VALIDATION DE SÉLECTION
    // ═══════════════════════════════════════════════════════════

    // Vérifier si une entité (ex: UE, Inscription) est déjà dans ce batch
    Optional<MigrationDecision> findByBatchIdAndSourceIdAndSourceType(
            Long batchId, Long sourceId, MigrationSourceType sourceType);

    // Vérifier si l'entité a déjà été migrée avec succès (pour éviter doublons à l'exécution)
    boolean existsByBatchIdAndSourceIdAndSourceTypeAndStatus(
            Long batchId, Long sourceId, MigrationSourceType sourceType, MigrationDecisionStatus status);

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES / COMPTAGE
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT COUNT(d) FROM MigrationDecision d WHERE d.batch.id = :batchId AND d.status = :status")
    long countByBatchIdAndStatus(@Param("batchId") Long batchId, @Param("status") MigrationDecisionStatus status);
}