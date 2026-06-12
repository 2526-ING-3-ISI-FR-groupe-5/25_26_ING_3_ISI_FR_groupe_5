package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository;


import org.apache.poi.sl.draw.geom.GuideIf;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeSemestre;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {
    List<Semestre> findByAnneeAcademiqueId(Long anneeAcademiqueId);

   // Optional<Semestre> findByAnneeAcademiqueIdAndActiveTrue(Long anneeAcademiqueId);
Optional<Semestre> findByAnneeAcademiqueIdAndActive(Long anneeAcademiqueId, Boolean active);
    Optional<Semestre> findByAnneeAcademiqueIdAndTypeSemestre(Long anneeAcademiqueId, TypeSemestre typeSemestre);

    boolean existsByAnneeAcademiqueIdAndTypeSemestre(Long anneeAcademiqueId, TypeSemestre typeSemestre);

    long countByAnneeAcademiqueId(Long anneeId);


    // OU, si tu préfères une @Query explicite (plus robuste) :
    @Query("SELECT s FROM Semestre s WHERE s.anneeAcademique.id = :anneeId AND s.active = true")
    Optional<Semestre> findByAnneeAcademiqueIdAndActiveTrue(@Param("anneeId") Long anneeId);

    Optional<Semestre> findByAnneeAcademique_Institut_IdAndActiveTrue(Long institutId);
}