package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.MigrationBatch;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.MigrationBatchStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface MigrationBatchRepository extends JpaRepository<MigrationBatch, Long> {

    List<MigrationBatch> findByInstitutId(Long institutId);

    List<MigrationBatch> findByInstitutIdAndStatus(Long institutId, MigrationBatchStatus status);

    boolean existsBySourceAnneeIdAndTargetAnneeId(Long sourceAnneeId, Long targetAnneeId);

    Optional<MigrationBatch> findBySourceAnneeIdAndTargetAnneeId(Long sourceAnneeId, Long targetAnneeId);

    /**
     * ✅ AJOUTÉ — Batches d'un institut triés par date décroissante, paginés.
     * Utilisé par le dashboard pour afficher l'historique récent (5 derniers).
     */
    Page<MigrationBatch> findByInstitutIdOrderByDateExecutionDesc(Long institutId, Pageable pageable);

    /**
     * ✅ AJOUTÉ — Batches rollbackables d'un institut (TERMINEE + rollbackPossible).
     * Utilisé pour afficher les boutons Publier/Rollback sur le dashboard.
     */
    List<MigrationBatch> findByInstitutIdAndStatusAndRollbackPossibleTrue(
            Long institutId, MigrationBatchStatus status);

    @EntityGraph(attributePaths = {
            "decisions",
            "decisions.targetAnnee",
            "decisions.targetSemestre",
            "sourceAnnee",
            "targetAnnee"
    })
    @Query("SELECT b FROM MigrationBatch b WHERE b.id = :id")
    Optional<MigrationBatch> findByIdWithDetails(@Param("id") Long id);
}