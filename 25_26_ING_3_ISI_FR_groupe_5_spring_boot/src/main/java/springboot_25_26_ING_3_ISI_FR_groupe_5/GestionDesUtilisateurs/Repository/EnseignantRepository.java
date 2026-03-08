package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

public interface EnseignantRepository extends JpaRepository<Enseignant, Long> {
}
