package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.DecisionFinAnnee;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES DE BASE
    // ═══════════════════════════════════════════════════════════
    List<Inscription> findByAnneeAcademiqueId(Long anneeId);
    List<Inscription> findByEtudiantId(Long etudiantId);
    List<Inscription> findByClasseId(Long classeId);
    Optional<Inscription> findByEtudiantIdAndAnneeAcademiqueId(Long etudiantId, Long anneeId);
    List<Inscription> findByClasseIdAndAnneeAcademiqueId(Long classeId, Long anneeId);
    boolean existsByEtudiantIdAndAnneeAcademiqueId(Long etudiantId, Long anneeId);
    List<Inscription> findByEtudiantIdOrderByAnneeAcademiqueNomDesc(Long etudiantId);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR DÉCISION & STATUT
    // ═══════════════════════════════════════════════════════════
    List<Inscription> findByAnneeAcademiqueIdAndDecisionFinAnnee(Long anneeId, DecisionFinAnnee decision);
    List<Inscription> findByAnneeAcademiqueIdAndStatut(Long anneeId, StatutInscription statut);
    List<Inscription> findByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);

    @Query("SELECT i FROM Inscription i WHERE i.anneeAcademique.id = :anneeId AND i.decisionFinAnnee = :decision")
    List<Inscription> findByAnneeAndDecision(@Param("anneeId") Long anneeId, @Param("decision") DecisionFinAnnee decision);

    // ═══════════════════════════════════════════════════════════
    // COMPTEURS
    // ═══════════════════════════════════════════════════════════
    long countByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS (Filtrage sécurisé)
    // ═══════════════════════════════════════════════════════════
    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.anneeAcademique.id = :anneeId 
        AND i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByAnneeAcademiqueIdAndInstitutId(@Param("anneeId") Long anneeId, @Param("institutId") Long institutId);

    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByInstitutId(@Param("institutId") Long institutId);

    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.classe.id = :classeId 
        AND i.anneeAcademique.id = :anneeId
        AND i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByClasseIdAndAnneeAcademiqueIdAndInstitutId(@Param("classeId") Long classeId, @Param("anneeId") Long anneeId, @Param("institutId") Long institutId);

    // ═══════════════════════════════════════════════════════════
    // PAGINATION AVEC @EntityGraph (Anti N+1 / LazyInit)
    // ═══════════════════════════════════════════════════════════
    @EntityGraph(attributePaths = {"etudiant", "classe", "anneeAcademique"})
    @Query("SELECT i FROM Inscription i WHERE i.anneeAcademique.id = :anneeId")
    Page<Inscription> findByAnneeAcademiqueIdPaginated(@Param("anneeId") Long anneeId, Pageable pageable);

    @EntityGraph(attributePaths = {"etudiant", "classe.niveau", "anneeAcademique"})
    @Query("SELECT i FROM Inscription i WHERE i.classe.id = :classeId AND i.anneeAcademique.id = :anneeId")
    Page<Inscription> findByClasseIdAndAnneeAcademiqueIdPaginated(@Param("classeId") Long classeId, @Param("anneeId") Long anneeId, Pageable pageable);

    @EntityGraph(attributePaths = {"etudiant", "classe.niveau.specialite", "anneeAcademique"})
    @Query("""
        SELECT i FROM Inscription i
        WHERE i.anneeAcademique.id = :anneeId
        AND i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    Page<Inscription> findByAnneeAcademiqueIdAndInstitutIdPaginated(@Param("anneeId") Long anneeId, @Param("institutId") Long institutId, Pageable pageable);
}