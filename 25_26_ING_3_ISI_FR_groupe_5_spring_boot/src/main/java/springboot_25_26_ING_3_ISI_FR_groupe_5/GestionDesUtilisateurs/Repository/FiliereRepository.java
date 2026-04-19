package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Filiere;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiliereRepository extends JpaRepository<Filiere, Long> {
    Optional<Filiere> findByNomAndEcoleId(String nom, Long ecoleId);
    List<Filiere> findByEcoleId(Long ecoleId);
    List<Filiere> findByCycleId(Long cycleId);
    List<Filiere> findByEcoleIdAndCycleId(Long ecoleId, Long cycleId);

    Optional<Filiere> findByCode(String code);
    boolean existsByCodeAndEcoleId(String code, Long ecoleId);

    List<Filiere> findByNomContainingIgnoreCase(String nom);
}
