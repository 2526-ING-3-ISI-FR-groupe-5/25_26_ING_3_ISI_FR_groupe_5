package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Institut;

import java.util.List;
import java.util.Optional;

public interface InstitutRepository extends JpaRepository<Institut, Long> {

    Optional<Institut> findByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCase(String nom);

    List<Institut> findByVilleContainingIgnoreCase(String ville);

    @Query("SELECT i FROM Institut i WHERE " +
            "LOWER(i.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.ville) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(i.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Institut> search(@Param("search") String search, Pageable pageable);
}