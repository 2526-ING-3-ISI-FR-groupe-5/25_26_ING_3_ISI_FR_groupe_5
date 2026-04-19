package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Etudiant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.Optional;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    Optional<Etudiant> findByEmail(String email);

    boolean existsByEmailContainingIgnoreCase(String email);

    // ✅ Recherche par année avec statut ACTIF
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

    // ✅ Tous statuts confondus (pour les rapports)
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
}