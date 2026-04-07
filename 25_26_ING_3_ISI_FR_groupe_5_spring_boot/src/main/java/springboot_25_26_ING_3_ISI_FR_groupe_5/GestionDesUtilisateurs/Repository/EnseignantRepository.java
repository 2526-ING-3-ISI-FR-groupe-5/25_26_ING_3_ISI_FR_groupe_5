package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

import java.util.Optional;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {

   Page<Enseignant> findByTypeEnseignant(String typeEnseignant, Pageable pageable);

   Page<Enseignant> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    @Query("""
    SELECT COUNT(DISTINCT enseignant)
    FROM ProgrammationUE pu
    JOIN pu.enseignants enseignant
    JOIN pu.semestre s
    JOIN s.anneeAcademique a  
    WHERE a.id = :anneeId
""")
    long countByAnnee(@Param("anneeId") Long anneeId);

 Page<Enseignant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
   boolean existsByEmail(String email);

   Optional<Enseignant> findByEmail(String email);
    // ✅ Ajouter
    @Query("""
    SELECT DISTINCT e FROM Enseignant e
    JOIN e.programmations p
    JOIN p.semestre s
    WHERE s.anneeAcademique.id = :anneeId
    AND (:recherche IS NULL OR :recherche = '' OR
        LOWER(e.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
        LOWER(e.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
        LOWER(e.email) LIKE LOWER(CONCAT('%', :recherche, '%')))
""")
    Page<Enseignant> searchByAnnee(
            @Param("anneeId") Long anneeId,
            @Param("recherche") String recherche,
            Pageable pageable
    );


}