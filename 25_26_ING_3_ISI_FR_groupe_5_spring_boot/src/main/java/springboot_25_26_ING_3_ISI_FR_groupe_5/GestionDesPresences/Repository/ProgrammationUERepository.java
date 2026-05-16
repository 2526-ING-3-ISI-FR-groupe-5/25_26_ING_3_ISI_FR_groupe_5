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
    // RECHERCHES DE BASE (avec @EntityGraph anti N+1)
    // ═══════════════════════════════════════════════════════════

    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseId(Long classeId);

    @EntityGraph(attributePaths = {"ue", "classe.niveau", "semestre.anneeAcademique", "enseignants"})
    List<ProgrammationUE> findBySemestreId(Long semestreId);

    @Query("SELECT p FROM ProgrammationUE p WHERE p.id = :id")
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    Optional<ProgrammationUE> findByIdWithDetails(@Param("id") Long id);

    // ═══════════════════════════════════════════════════════════
    // PAR CLASSE + ANNÉE (remplace .active par ID explicite)
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

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUT (filtrage sécurisé via chemin académique)
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
    // PAR ENSEIGNANT (avec filtrage institut optionnel)
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

    // ✅ Méthode dérivée simple (sans @Query) pour compatibilité
    List<ProgrammationUE> findBySemestre_AnneeAcademique_Id(Long anneeId);

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
    // PAGINATION AVEC @EntityGraph (Anti N+1 / LazyInit)
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
    // MIGRATION SÉLECTIVE (préparation du clonage N → N+1)
    // ═══════════════════════════════════════════════════════════

    /**
     * Trouve les programmations d'une année source pour un institut donné.
     * Utilisé pour la sélection manuelle avant migration vers N+1.
     */
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.semestre.anneeAcademique.id = :sourceAnneeId
        AND p.classe.niveau.filiere.ecole.institut.id = :institutId
        """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findMigrablesBySourceAnneeAndInstitut(
            @Param("sourceAnneeId") Long sourceAnneeId,
            @Param("institutId") Long institutId);

    /**
     * Vérifie si une programmation équivalente existe déjà dans l'année cible.
     * Utilisé pour éviter les doublons lors du clonage.
     */
    boolean existsByUeIdAndClasseIdAndSemestreId(
            Long ueId, Long classeId, Long semestreId);

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES DÉPRÉCIÉES (à supprimer après migration complète)
    // ═══════════════════════════════════════════════════════════

    /**
     * @deprecated Utiliser findByClasseIdAndAnneeAcademiqueId avec InstitutContexteActif
     */
    @Deprecated
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.active = true
        ORDER BY p.ue.nom ASC
        """)
    List<ProgrammationUE> findByClasseIdAndAnneeActive(@Param("classeId") Long classeId);

    /**
     * @deprecated Utiliser findByEnseignantIdAndInstitutId avec filtrage explicite
     */
    @Deprecated
    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
        """)
    List<ProgrammationUE> findByEnseignantId(@Param("enseignantId") Long enseignantId);

    /**
     * @deprecated Utiliser avec filtrage institut explicite
     */
    @Deprecated
    @Query("""
        SELECT DISTINCT p.classe FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
        """)
    List<Classe> findClassesByEnseignantId(@Param("enseignantId") Long enseignantId);

    /**
     * @deprecated Utiliser avec filtrage institut + année explicite
     */
    @Deprecated
    @Query("""
        SELECT p FROM ProgrammationUE p 
        JOIN p.enseignants e 
        WHERE e.id = :enseignantId 
        AND p.semestre.active = true
        """)
    List<ProgrammationUE> findByEnseignantsIdAndSemestreActifTrue(
            @Param("enseignantId") Long enseignantId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
        """)
    @EntityGraph(attributePaths = {"ue", "classe", "semestre", "enseignants"})
    List<ProgrammationUE> findByClasseAndAnnee(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId);
}
