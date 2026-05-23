package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.SessionAppel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAppelRepository extends JpaRepository<SessionAppel, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR CLASSE
    // ═══════════════════════════════════════════════════════════

    /**
     * Session normale active pour une classe (QR Code ou PIN).
     */
    @Query("""
    SELECT DISTINCT s FROM SessionAppel s
    JOIN FETCH s.plageHoraire
    WHERE s.plageHoraire.classe.id = :classeId
    AND s.actif = true
    AND s.typeSession = 'NORMALE'
""")
    Optional<SessionAppel> findActiveByClasseId(@Param("classeId") Long classeId);

    /**
     * Session offline active pour une classe.
     */
    @Query("""
    SELECT DISTINCT s FROM SessionAppel s
    JOIN FETCH s.plageHoraire
    WHERE s.plageHoraire.classe.id = :classeId
    AND s.actif = true
    AND s.typeSession = 'OFFLINE'
""")
    Optional<SessionAppel> findOfflineActiveByClasseId(@Param("classeId") Long classeId);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR PLAGE HORAIRE
    // ═══════════════════════════════════════════════════════════

    /**
     * Toutes les sessions d'une plage horaire.
     */
    List<SessionAppel> findByPlageHoraireId(Long plageHoraireId);

    /**
     * Session active d'une plage horaire (peu importe le type).
     */
    Optional<SessionAppel> findByPlageHoraireIdAndActifTrue(Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    /**
     * Sessions d'un enseignant sur une période donnée.
     */
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

    /**
     * Trouve toutes les sessions actives mais expirées.
     */
    @Query("""
        SELECT s FROM SessionAppel s
        WHERE s.actif = true
        AND s.dateExpiration < :now
    """)
    List<SessionAppel> findSessionsExpirees(@Param("now") LocalDateTime now);

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES
    // ═══════════════════════════════════════════════════════════

    /**
     * Compte le nombre de sessions terminées pour une plage.
     */
    long countByPlageHoraireIdAndCoursTermineTrue(Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    /**
     * Toutes les sessions d'un institut.
     */
    @Query("""
        SELECT s FROM SessionAppel s
        WHERE s.plageHoraire.classe.niveau.filiere.ecole.institut.id = :institutId
        ORDER BY s.dateGeneration DESC
    """)
    List<SessionAppel> findByInstitutId(@Param("institutId") Long institutId);
}