package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgrammationUERepository extends JpaRepository<ProgrammationUE, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES DE BASE
    // ═══════════════════════════════════════════════════════════

    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseId(Long classeId);

    @EntityGraph(attributePaths = {"ue", "classe.niveau", "semestre.anneeAcademique", "enseignants"})
    List<ProgrammationUE> findBySemestreId(Long semestreId);

    @Query("SELECT p FROM ProgrammationUE p WHERE p.id = :id")
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    Optional<ProgrammationUE> findByIdWithDetails(@Param("id") Long id);

    boolean existsByUeIdAndClasseIdAndSemestreId(Long ueId, Long classeId, Long semestreId);

    List<ProgrammationUE> findBySemestre_AnneeAcademique_Id(Long anneeId);

    // ═══════════════════════════════════════════════════════════
    // PAR CLASSE + ANNÉE
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
        ORDER BY p.ue.nom ASC
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseIdAndAnneeAcademiqueId(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.semestre.anneeAcademique.id = :anneeId
        ORDER BY p.ue.nom ASC
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByAnneeAcademiqueId(@Param("anneeId") Long anneeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseAndAnnee(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUT
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.niveau.filiere.ecole.institut.id = :institutId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByAnneeAcademiqueIdAndInstitutId(
            @Param("anneeId") Long anneeId,
            @Param("institutId") Long institutId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre"})
    List<ProgrammationUE> findByEnseignantIdAndInstitutId(
            @Param("enseignantId") Long enseignantId,
            @Param("institutId") Long institutId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
        AND p.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseIdAndAnneeAcademiqueIdAndInstitutId(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId,
            @Param("institutId") Long institutId);

    // ═══════════════════════════════════════════════════════════
    // PAR ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre"})
    List<ProgrammationUE> findByEnseignantAndAnnee(
            @Param("enseignantId") Long enseignantId,
            @Param("anneeId") Long anneeId);

    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.id = :semestreId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre"})
    List<ProgrammationUE> findByEnseignantIdAndSemestreId(
            @Param("enseignantId") Long enseignantId,
            @Param("semestreId") Long semestreId);

    // ═══════════════════════════════════════════════════════════
    // PAR UE
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.ue.id = :ueId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByUeAndAnnee(
            @Param("ueId") Long ueId,
            @Param("anneeId") Long anneeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.ue.id = :ueId
        AND p.semestre.id = :semestreId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByUeIdAndSemestreId(
            @Param("ueId") Long ueId,
            @Param("semestreId") Long semestreId);

    // ═══════════════════════════════════════════════════════════
    // PAGINATION
    // ═══════════════════════════════════════════════════════════

    @EntityGraph(attributePaths = {"ue", "classe.niveau", "semestre.anneeAcademique", "enseignants"})
    @Query("SELECT p FROM ProgrammationUE p WHERE p.semestre.anneeAcademique.id = :anneeId")
    Page<ProgrammationUE> findByAnneeAcademiqueIdPaginated(
            @Param("anneeId") Long anneeId, Pageable pageable);

    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    Page<ProgrammationUE> findByClasseIdAndAnneeAcademiqueIdPaginated(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId,
            Pageable pageable);

    @EntityGraph(attributePaths = {"ue", "classe.niveau.specialite", "semestre.anneeAcademique", "enseignants"})
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.semestre.anneeAcademique.id = :anneeId
        AND p.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    Page<ProgrammationUE> findByAnneeAcademiqueIdAndInstitutIdPaginated(
            @Param("anneeId") Long anneeId,
            @Param("institutId") Long institutId,
            Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // MIGRATION SÉLECTIVE
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.semestre.anneeAcademique.id = :sourceAnneeId
        AND p.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findMigrablesBySourceAnneeAndInstitut(
            @Param("sourceAnneeId") Long sourceAnneeId,
            @Param("institutId") Long institutId);

    // ═══════════════════════════════════════════════════════════
    // ✅ AJOUTÉ — PAR ENSEIGNANT + ANNÉE (pour consultation N-1, N-2...)
    // Remplace findByEnseignantsIdAndSemestreActifTrue() dans StatsService
    // pour permettre la consultation de n'importe quelle année historique.
    // ═══════════════════════════════════════════════════════════

    /**
     * Programmations d'un enseignant pour une année précise.
     * Utilisé par StatsService.getProgressionEnseignant(enseignantId, anneeId).
     * null anneeId → utiliser findByEnseignantAndSemestreActif() à la place.
     */
    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.id = :anneeId
        ORDER BY p.ue.nom ASC
    """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre"})
    List<ProgrammationUE> findByEnseignantIdAndAnneeId(
            @Param("enseignantId") Long enseignantId,
            @Param("anneeId") Long anneeId);

    // ═══════════════════════════════════════════════════════════
    // DÉPRÉCIÉES — à supprimer après migration complète
    // ═══════════════════════════════════════════════════════════

    /** @deprecated Utiliser findByClasseIdAndAnneeAcademiqueId */
    @Deprecated
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.active = true
        ORDER BY p.ue.nom ASC
    """)
    List<ProgrammationUE> findByClasseIdAndAnneeActive(@Param("classeId") Long classeId);

    /** @deprecated Utiliser findByEnseignantIdAndInstitutId */
    @Deprecated
    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
    """)
    List<ProgrammationUE> findByEnseignantId(@Param("enseignantId") Long enseignantId);

    /** @deprecated Utiliser findByEnseignantIdAndAnneeId */
    @Deprecated
    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.active = true
    """)
    List<ProgrammationUE> findByEnseignantsIdAndSemestreActifTrue(
            @Param("enseignantId") Long enseignantId);

    /** @deprecated Utiliser avec filtrage institut + année explicite */
    @Deprecated
    @Query("""
        SELECT DISTINCT p.classe FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
    """)
    List<Classe> findClassesByEnseignantId(@Param("enseignantId") Long enseignantId);
}