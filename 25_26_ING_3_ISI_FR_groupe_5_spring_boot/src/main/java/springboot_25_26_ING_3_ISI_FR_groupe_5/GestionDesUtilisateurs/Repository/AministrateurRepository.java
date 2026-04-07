package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Administrateur;

import java.util.List;
import java.util.Optional;

public interface AministrateurRepository extends JpaRepository<Administrateur, Long> {


    Optional<Administrateur> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Administrateur> findByFonction(String fonction);

}
