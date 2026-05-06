package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.SessionAppel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAppelRepository extends JpaRepository<SessionAppel, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR PLAGE HORAIRE
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT s FROM SessionAppel s WHERE s.plageHoraire.classe.id = :classeId AND s.actif = true")
    Optional<SessionAppel> findActiveByClasseId(@Param("classeId") Long classeId);

    List<SessionAppel> findByPlageHoraireId(Long plageHoraireId);

    // Session active d'une plage horaire
    Optional<SessionAppel> findByPlageHoraireIdAndActifTrue(Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT s FROM SessionAppel s
        WHERE s.enseignant.id = :enseignantId
        AND s.dateGeneration BETWEEN :debut AND :fin
        ORDER BY s.dateGeneration DESC
    """)
    List<SessionAppel> findByEnseignantAndPeriode(
            @Param("enseignantId") Long enseignantId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    // ═══════════════════════════════════════════════════════════
    // SESSIONS EXPIRÉES (pour nettoyage)
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT s FROM SessionAppel s
        WHERE s.actif = true
        AND s.dateExpiration < :now
    """)
    List<SessionAppel> findSessionsExpirees(@Param("now") LocalDateTime now);

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES
    // ═══════════════════════════════════════════════════════════


    long countByPlageHoraireIdAndCoursTermineTrue(Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT s FROM SessionAppel s
        WHERE s.plageHoraire.classe.niveau.filiere.ecole.institut.id = :institutId
        ORDER BY s.dateGeneration DESC
    """)
    List<SessionAppel> findByInstitutId(@Param("institutId") Long institutId);
}