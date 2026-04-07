package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.Optional;

public interface AssistantRepository extends JpaRepository<AssistantPedagogique,Long> {
    boolean existsByEmail(String email);
    Page<AssistantPedagogique> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom, Pageable pageable);
    Optional<AssistantPedagogique> findByEmail(String email);
}
