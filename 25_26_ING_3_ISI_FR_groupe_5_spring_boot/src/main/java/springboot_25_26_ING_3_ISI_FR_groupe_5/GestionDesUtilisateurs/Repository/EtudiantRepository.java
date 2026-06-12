package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.List;
import java.util.Optional;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    Optional<Etudiant> findByEmail(String email);
    boolean existsByEmailContainingIgnoreCase(String email);

    @Query("""
        SELECT DISTINCT e FROM Etudiant e
        JOIN e.inscriptions i
        WHERE i.anneeAcademique.id = :anneeId
        AND i.statut = 'ACTIF'
        AND (:recherche IS NULL OR :recherche = '' OR
            LOWER(e.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.email) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.matricule) LIKE LOWER(CONCAT('%', :recherche, '%')))
    """)
    Page<Etudiant> searchByAnnee(
            @Param("anneeId") Long anneeId,
            @Param("recherche") String recherche,
            Pageable pageable
    );

    @Query("""
        SELECT DISTINCT e FROM Etudiant e
        JOIN e.inscriptions i
        WHERE i.anneeAcademique.id = :anneeId
        AND (:statut IS NULL OR i.statut = :statut)
        AND (:recherche IS NULL OR :recherche = '' OR
            LOWER(e.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.email) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.matricule) LIKE LOWER(CONCAT('%', :recherche, '%')))
    """)
    Page<Etudiant> searchByAnneeAndStatut(
            @Param("anneeId") Long anneeId,
            @Param("statut") StatutInscription statut,
            @Param("recherche") String recherche,
            Pageable pageable
    );

    // 🆕 Pour le multi-instituts - Version List (selects)
    @Query("SELECT e FROM Etudiant e WHERE e.institut.id = :institutId")
    List<Etudiant> findByInstitutId(@Param("institutId") Long institutId);

    List<Etudiant> findByClasseIdAndActiveTrue(Long classeId);

    // 🆕 Version Pageable (listes paginées)
    @Query("SELECT e FROM Etudiant e WHERE e.institut.id = :institutId")
    Page<Etudiant> findByInstitutId(@Param("institutId") Long institutId, Pageable pageable);

    // 🆕 Étudiants actifs par institut
    @Query("SELECT e FROM Etudiant e WHERE e.institut.id = :institutId AND e.active = true")
    Page<Etudiant> findActifsByInstitutId(@Param("institutId") Long institutId, Pageable pageable);

    // 🆕 Recherche par institut avec filtre
    @Query("""
        SELECT e FROM Etudiant e 
        WHERE e.institut.id = :institutId 
        AND (:recherche IS NULL OR :recherche = '' OR
            LOWER(e.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.email) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
            LOWER(e.matricule) LIKE LOWER(CONCAT('%', :recherche, '%')))
    """)
    Page<Etudiant> searchByInstitutId(
            @Param("institutId") Long institutId,
            @Param("recherche") String recherche,
            Pageable pageable
    );
}
