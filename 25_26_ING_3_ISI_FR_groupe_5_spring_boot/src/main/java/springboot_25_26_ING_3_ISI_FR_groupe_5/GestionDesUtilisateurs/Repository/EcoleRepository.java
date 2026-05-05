package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;

import java.util.List;
import java.util.Optional;

public interface EcoleRepository extends JpaRepository<Ecole, Long> {

    // Recherche par l'objet Institut complet
    List<Ecole> findByInstitut(Institut institut);

    // ✅ Navigation via l'objet institut puis son id (avec underscore)
    List<Ecole> findByInstitut_Id(Long institutId);

    // ✅ Navigation via l'objet institut puis son id (avec underscore)
    Optional<Ecole> findByNomAndInstitut_Id(String nom, Long institutId);

    // ✅ Navigation via l'objet institut puis son id (avec underscore)
    boolean existsByNomAndInstitut_Id(String nom, Long institutId);

    // ✅ Navigation via l'objet institut puis son id (avec underscore)
    Page<Ecole> findByInstitut_Id(Long institutId, Pageable pageable);

    // Requête de recherche
    @Query("SELECT e FROM Ecole e WHERE " +
            "LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ecole> search(@Param("search") String search, Pageable pageable);
}