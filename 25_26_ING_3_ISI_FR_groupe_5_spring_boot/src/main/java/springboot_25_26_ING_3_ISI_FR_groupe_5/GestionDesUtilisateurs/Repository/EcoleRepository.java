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

    List<Ecole> findByInstitut(Institut institut);

    List<Ecole> findByInstitutId(Long institutId);

    Optional<Ecole> findByNomAndInstitutId(String nom, Long institutId);

    boolean existsByNomAndInstitutId(String nom, Long institutId);

    Page<Ecole> findByInstitutId(Long institutId, Pageable pageable);

    @Query("SELECT e FROM Ecole e WHERE " +
            "LOWER(e.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Ecole> search(@Param("search") String search, Pageable pageable);
}