package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Specialite;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialiteRepository extends JpaRepository<Specialite, Long> {

    Optional<Specialite> findByCode(String code);

    boolean existsByCode(String code);

    List<Specialite> findByFiliereId(Long filiereId);

    @Query("SELECT s FROM Specialite s WHERE s.nom LIKE %:keyword% OR s.code LIKE %:keyword%")
    Page<Specialite> search(@Param("keyword") String keyword, Pageable pageable);
}