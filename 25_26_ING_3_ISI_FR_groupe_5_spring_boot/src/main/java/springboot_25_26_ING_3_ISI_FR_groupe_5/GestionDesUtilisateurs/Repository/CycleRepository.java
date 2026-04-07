package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeCycle;

import java.util.List;
import java.util.Optional;

@Repository
public interface CycleRepository extends JpaRepository<Cycle, Long> {
    
    Optional<Cycle> findByTypeCycle(TypeCycle typeCycle);

    boolean existsByTypeCycle(TypeCycle typeCycle);

    @Query("SELECT c FROM Cycle c JOIN c.filieres f WHERE f.ecole.id = :ecoleId")
    List<Cycle> findByEcoleId(@Param("ecoleId") Long ecoleId);
}