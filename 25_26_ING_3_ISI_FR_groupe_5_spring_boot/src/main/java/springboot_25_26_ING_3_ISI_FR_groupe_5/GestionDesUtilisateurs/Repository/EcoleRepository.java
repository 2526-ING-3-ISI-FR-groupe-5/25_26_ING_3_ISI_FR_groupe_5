package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Ecole;

import java.util.Optional;

@Repository
public interface EcoleRepository extends JpaRepository<Ecole, Long> {

    Optional<Ecole> findByNom(String nom);
    boolean existsByNom(String nom);
    boolean existsByEmail(String email);
}
