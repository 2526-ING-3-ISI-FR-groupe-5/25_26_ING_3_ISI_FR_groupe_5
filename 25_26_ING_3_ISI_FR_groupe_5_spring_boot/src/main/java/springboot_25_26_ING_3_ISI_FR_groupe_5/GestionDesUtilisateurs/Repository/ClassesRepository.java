package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassesRepository extends JpaRepository<Classe, Long> {

    // ✅ Trouver toutes les classes d'un niveau
    List<Classe> findByNiveauId(Long niveauId);

    // ✅ Recherche par nom
    List<Classe> findByNomContainingIgnoreCase(String nom);

    Optional<Classe> findByNom(String nom);

    // ✅ Recherche par année académique (avec pagination)
    @Query("""
        SELECT DISTINCT c FROM Classe c
        JOIN c.inscriptions i
        WHERE i.anneeAcademique.id = :anneeId
        AND (:nom IS NULL OR :nom = '' OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%')))
    """)
    Page<Classe> searchByAnnee(
            @Param("anneeId") Long anneeId,
            @Param("nom") String nom,
            Pageable pageable
    );

    // ✅ Trouver les classes par spécialité et niveau
    @Query("SELECT c FROM Classe c WHERE c.niveau.id = :niveauId AND c.niveau.specialite.id = :specialiteId")
    List<Classe> findBySpecialiteIdAndNiveauId(
            @Param("specialiteId") Long specialiteId,
            @Param("niveauId") Long niveauId
    );

    // ✅ Trouver les classes d'une filière (via le niveau)
    @Query("SELECT c FROM Classe c WHERE c.niveau.filiere.id = :filiereId")
    List<Classe> findByFiliereId(@Param("filiereId") Long filiereId);

    // ✅ Trouver les classes d'un cycle (via le niveau)
    @Query("SELECT c FROM Classe c WHERE c.niveau.filiere.cycle.id = :cycleId")
    List<Classe> findByCycleId(@Param("cycleId") Long cycleId);

    @Query("SELECT c FROM Classe c WHERE c.niveau.id = :niveauId")
    List<Classe> findByNiveau(@Param("niveauId") Long niveauId);

    boolean existsByNom(String nom);

}