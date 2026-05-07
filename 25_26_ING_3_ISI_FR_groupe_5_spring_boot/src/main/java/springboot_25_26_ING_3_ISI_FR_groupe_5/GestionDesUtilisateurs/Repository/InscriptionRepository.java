package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.DecisionFinAnnee;
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
    // RECHERCHES PAR DÉCISION
    // ═══════════════════════════════════════════════════════════

    List<Inscription> findByAnneeAcademiqueIdAndDecisionFinAnnee(Long anneeId, DecisionFinAnnee decision);

    @Query("SELECT i FROM Inscription i WHERE i.anneeAcademique.id = :anneeId AND i.decisionFinAnnee = :decision")
    List<Inscription> findByAnneeAndDecision(@Param("anneeId") Long anneeId, @Param("decision") DecisionFinAnnee decision);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR STATUT
    // ═══════════════════════════════════════════════════════════

    List<Inscription> findByAnneeAcademiqueIdAndStatut(Long anneeId, StatutInscription statut);

    // ✅ Méthode pour les étudiants actifs d'une classe
    List<Inscription> findByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);

    // ═══════════════════════════════════════════════════════════
    // COMPTEURS
    // ═══════════════════════════════════════════════════════════

    long countByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    /**
     * Trouve toutes les inscriptions d'une année académique pour un institut donné
     * L'institut est remonté via : inscription → classe → niveau → filière → école → institut
     */
    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.anneeAcademique.id = :anneeId 
        AND i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByAnneeAcademiqueIdAndInstitutId(
            @Param("anneeId") Long anneeId,
            @Param("institutId") Long institutId
    );

    /**
     * Trouve toutes les inscriptions d'un institut (toutes années confondues)
     */
    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByInstitutId(@Param("institutId") Long institutId);

    /**
     * Trouve les inscriptions d'une classe pour un institut (vérification de sécurité)
     */
    @Query("""
        SELECT i FROM Inscription i 
        WHERE i.classe.id = :classeId 
        AND i.anneeAcademique.id = :anneeId
        AND i.classe.niveau.filiere.ecole.institut.id = :institutId
    """)
    List<Inscription> findByClasseIdAndAnneeAcademiqueIdAndInstitutId(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId,
            @Param("institutId") Long institutId
    );
}