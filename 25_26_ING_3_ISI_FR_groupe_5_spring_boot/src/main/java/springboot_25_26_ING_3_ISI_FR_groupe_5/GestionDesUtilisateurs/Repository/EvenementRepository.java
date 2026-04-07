package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Evenement;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeEvenement;

import java.time.LocalDate;
import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement , Long > {
    List<Evenement> findByAnneeAcademiqueId(Long anneeId);
    List<Evenement> findByAnneeAcademiqueIdAndType(Long anneeId, TypeEvenement type);

    @Query("SELECT e FROM Evenement e WHERE e.anneeAcademique.id = :anneeId AND e.dateDebut <= :date AND e.dateFin >= :date")
    List<Evenement> findEvenementsActifsALaDate(
            @Param("anneeId") Long anneeId,
            @Param("date") LocalDate date
    );
}
