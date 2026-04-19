package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSemestre;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemestreRepository extends JpaRepository<Semestre, Long> {
    List<Semestre> findByAnneeAcademiqueId(Long anneeAcademiqueId);

    Optional<Semestre> findByAnneeAcademiqueIdAndActifTrue(Long anneeAcademiqueId);

    Optional<Semestre> findByAnneeAcademiqueIdAndTypeSemestre(Long anneeAcademiqueId, TypeSemestre typeSemestre);

    boolean existsByAnneeAcademiqueIdAndTypeSemestre(Long anneeAcademiqueId, TypeSemestre typeSemestre);

    long countByAnneeAcademiqueId(Long anneeId);
}