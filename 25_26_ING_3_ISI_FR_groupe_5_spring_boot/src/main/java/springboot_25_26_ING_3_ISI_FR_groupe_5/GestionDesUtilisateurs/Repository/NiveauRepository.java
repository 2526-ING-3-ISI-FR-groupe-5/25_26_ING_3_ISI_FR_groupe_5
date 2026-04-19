package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Niveau;

import java.util.List;
import java.util.Optional;

@Repository
public interface NiveauRepository extends JpaRepository<Niveau, Long> {
    // Par filière


    Optional<Niveau> findByNomAndFiliereId(String nom, Long filiereId);
    List<Niveau> findByFiliereIdOrderByOrdreAsc(Long filiereId);

    List<Niveau> findByFiliereIdAndSpecialiteIsNullOrderByOrdreAsc(Long filiereId);

    List<Niveau> findByFiliereIdAndSpecialiteIsNotNull(Long filiereId);

    Optional<Niveau> findByFiliereIdAndOrdreAndSpecialiteIsNull(Long filiereId, Integer ordre);

    // Pour la migration — niveau suivant avec spécialité
    List<Niveau> findByFiliereIdAndOrdreAndSpecialiteIsNotNull(Long filiereId, Integer ordre);

    @Query("SELECT n FROM Niveau n WHERE n.specialite.id = :specialiteId")
    List<Niveau> findBySpecialiteId(@Param("specialiteId") Long specialiteId);

    boolean existsByCodeAndFiliereId(String code, Long filiereId);

    Optional<Niveau> findByFiliereIdAndOrdre(Long filiereId, Integer ordre);

}