package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;

import java.util.List;
import java.util.Optional;

@Repository
public interface UERepository extends JpaRepository<UE, Long> {

    Optional<UE> findByCode(String code);
    List<UE> findByNomContainingIgnoreCase(String nom);
    List<UE> findBySpecialiteId(Long specialiteId);

    // ✅ Filtrage par institut (chemin académique)
    @Query("SELECT u FROM UE u WHERE u.specialite.filiere.ecole.institut.id = :institutId")
    List<UE> findByInstitutId(@Param("institutId") Long institutId);

    // ✅ Recherche paginée avec chargement optimisé
    @EntityGraph(attributePaths = {"specialite", "specialite.filiere"})
    @Query("SELECT u FROM UE u WHERE u.specialite.filiere.ecole.institut.id = :institutId")
    List<UE> findByInstitutIdWithDetails(@Param("institutId") Long institutId);

    // ✅ Filtrage par année académique (via programmations)
    @Query("""
        SELECT DISTINCT u FROM UE u
        LEFT JOIN u.programmations p
        LEFT JOIN p.semestre s
        WHERE s.anneeAcademique.id = :anneeId
        """)
    List<UE> findByAnneeAcademiqueId(@Param("anneeId") Long anneeId);

}