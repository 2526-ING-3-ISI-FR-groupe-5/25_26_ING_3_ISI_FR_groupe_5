package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.InstitutContexteActif;

import java.util.Optional;

@Repository
public interface InstitutContexteActifRepository extends JpaRepository<InstitutContexteActif, Long> {

    /** Trouve le contexte actif pour un institut donné */
    Optional<InstitutContexteActif> findByInstitutId(Long institutId);

    /** Trouve le contexte pour un institut + une année spécifique */
    Optional<InstitutContexteActif> findByInstitutIdAndAnneeAcademiqueId(Long institutId, Long anneeId);
}