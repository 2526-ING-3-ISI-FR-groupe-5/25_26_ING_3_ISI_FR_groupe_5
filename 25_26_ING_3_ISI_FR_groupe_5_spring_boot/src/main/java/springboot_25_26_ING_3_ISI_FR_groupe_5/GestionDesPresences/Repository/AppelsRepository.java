package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppelsRepository extends JpaRepository<Appels, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES DE BASE
    // ═══════════════════════════════════════════════════════════

    List<Appels> findByEtudiantId(Long etudiantId);
    List<Appels> findBySessionAppelId(Long sessionId);
    List<Appels> findByPlageHoraireId(Long plageHoraireId);
    List<Appels> findByPlageHoraireIdAndStatut(Long plageHoraireId, StatutPresence statut);
    List<Appels> findBySessionAppelIdAndStatut(Long sessionId, StatutPresence statut);
    List<Appels> findByEtudiantIdAndStatut(Long etudiantId, StatutPresence statut);
    Optional<Appels> findByEtudiantIdAndPlageHoraireId(Long etudiantId, Long plageHoraireId);

    /**
     * Compte les appels regroupés par statut pour une plage horaire.
     * Une seule requête SQL au lieu d'une par statut.
     * Retourne une liste de [StatutPresence, Long] — à transformer en Map côté service.
     */
    @Query("""
        SELECT a.statut, COUNT(a) FROM Appels a
        WHERE a.plageHoraire.id = :plageHoraireId
        GROUP BY a.statut
    """)
    List<Object[]> countByPlageGroupedByStatut(@Param("plageHoraireId") Long plageHoraireId);

    @Query("SELECT a FROM Appels a JOIN FETCH a.plageHoraire WHERE a.enseignant.id = :enseignantId ORDER BY a.dateValidation DESC")
    Page<Appels> findByEnseignantIdWithPlage(@Param("enseignantId") Long enseignantId, Pageable pageable);

    @Query("SELECT a FROM Appels a JOIN FETCH a.etudiant JOIN FETCH a.plageHoraire WHERE a.id = :id")
    Appels findByIdWithDetails(@Param("id") Long id);

    /**
     * ✅ AJOUTÉ — Évite le problème d'effet N+1 en chargeant d'un coup
     * l'étudiant, l'institut, l'enseignant et la plage horaire pour le mapping.
     */
    @Query("""
        SELECT a FROM Appels a 
        JOIN FETCH a.etudiant e
        JOIN FETCH e.institut i
        JOIN FETCH a.plageHoraire p
        LEFT JOIN FETCH a.enseignant ens
        WHERE a.plageHoraire.id = :plageHoraireId
    """)
    List<Appels> findByPlageHoraireIdWithDetails(@Param("plageHoraireId") Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT a FROM Appels a WHERE a.etudiant.institut.id = :institutId ORDER BY a.dateValidation DESC")
    Page<Appels> findByInstitutId(@Param("institutId") Long institutId, Pageable pageable);

    @Query("""
        SELECT a FROM Appels a
        WHERE a.etudiant.institut.id = :institutId
        AND a.present = false
        AND a.justificatif IS NULL
        ORDER BY a.dateValidation DESC
    """)
    Page<Appels> findAbsencesNonJustifieesByInstitutId(
            @Param("institutId") Long institutId, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES — SEMESTRE ACTIF (conserver pour compatibilité)
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT COUNT(a) FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.active = true
        AND a.statut = :statut
    """)
    long countByEtudiantAndStatutAndSemestreActif(
            @Param("etudiantId") Long etudiantId,
            @Param("statut") StatutPresence statut);

    @Query("""
        SELECT COUNT(a) FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.active = true
        AND a.statut = 'ABSENT'
        AND a.justificatif IS NULL
    """)
    long countAbsencesNonJustifieesByEtudiant(@Param("etudiantId") Long etudiantId);

    @Query("SELECT COUNT(a) FROM Appels a WHERE a.etudiant.id = :etudiantId AND a.present = false")
    long countAbsencesByEtudiant(@Param("etudiantId") Long etudiantId);

    // ═══════════════════════════════════════════════════════════
    // ✅ AJOUTÉ — STATISTIQUES PAR ANNÉE (pour consultation N-1, N-2...)
    // ═══════════════════════════════════════════════════════════

    /**
     * Compte les appels d'un statut donné pour un étudiant sur une année précise.
     */
    @Query("""
        SELECT COUNT(a) FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.anneeAcademique.id = :anneeId
        AND a.statut = :statut
    """)
    long countByEtudiantAndStatutAndAnnee(
            @Param("etudiantId") Long etudiantId,
            @Param("statut") StatutPresence statut,
            @Param("anneeId") Long anneeId);

    /**
     * Compte les absences non justifiées d'un étudiant sur une année précise.
     */
    @Query("""
        SELECT COUNT(a) FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.anneeAcademique.id = :anneeId
        AND a.statut = 'ABSENT'
        AND a.justificatif IS NULL
    """)
    long countAbsencesNonJustifieesByEtudiantAndAnnee(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeId") Long anneeId);

    /**
     * Somme des heures de présence d'un étudiant sur une année précise.
     */
    @Query("""
        SELECT COALESCE(SUM(a.nbHeuresPresent), 0) FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.anneeAcademique.id = :anneeId
    """)
    int sumHeuresPresentByEtudiantAndAnnee(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeId") Long anneeId);

    /**
     * Tous les appels d'un étudiant pour une année précise.
     */
    @Query("""
        SELECT a FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.anneeAcademique.id = :anneeId
        ORDER BY a.plageHoraire.heureDebut ASC
    """)
    List<Appels> findByEtudiantIdAndAnneeId(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeId") Long anneeId);

    /**
     * Tous les appels d'un étudiant pour une année et un statut précis.
     */
    @Query("""
        SELECT a FROM Appels a
        WHERE a.etudiant.id = :etudiantId
        AND a.plageHoraire.semestre.anneeAcademique.id = :anneeId
        AND a.statut = :statut
        ORDER BY a.plageHoraire.heureDebut ASC
    """)
    List<Appels> findByEtudiantIdAndAnneeIdAndStatut(
            @Param("etudiantId") Long etudiantId,
            @Param("anneeId") Long anneeId,
            @Param("statut") StatutPresence statut);

    // ═══════════════════════════════════════════════════════════
    // MISE À JOUR BULK
    // ═══════════════════════════════════════════════════════════

    @Modifying
    @Query("""
        UPDATE Appels a
        SET a.statut = :statut
        WHERE a.justificatif.id = :justificatifId
    """)
    void updateStatutByJustificatifId(
            @Param("justificatifId") Long justificatifId,
            @Param("statut") StatutPresence statut);
}