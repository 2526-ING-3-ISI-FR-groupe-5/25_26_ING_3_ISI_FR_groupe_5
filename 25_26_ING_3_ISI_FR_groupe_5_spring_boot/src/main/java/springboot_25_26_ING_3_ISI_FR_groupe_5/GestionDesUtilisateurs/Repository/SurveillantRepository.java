package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Surveillant;

import java.util.Optional;

public interface SurveillantRepository extends JpaRepository<Surveillant, Long> {
    boolean existsByEmail(String email);
    Optional<Surveillant> findByEmail(String email);
}
