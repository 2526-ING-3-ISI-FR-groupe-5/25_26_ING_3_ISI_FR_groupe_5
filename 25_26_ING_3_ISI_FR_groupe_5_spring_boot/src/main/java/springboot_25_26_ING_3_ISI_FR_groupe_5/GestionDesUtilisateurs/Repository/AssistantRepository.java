package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.List;
import java.util.Optional;

public interface AssistantRepository extends JpaRepository<AssistantPedagogique, Long> {

    boolean existsByEmail(String email);

    Optional<AssistantPedagogique> findByEmail(String email);

    Page<AssistantPedagogique> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom, Pageable pageable);

    /**
     * ✅ AJOUTÉ — Trouve tous les assistants d'un institut.
     * Chemin : AssistantPedagogique → classes → niveau → filiere → ecole → institut
     * Utilisé par MigrationService.migrerTousLesAssistants()
     */
    @Query("SELECT DISTINCT a FROM AssistantPedagogique a " +
            "JOIN a.classes c " +
            "JOIN c.niveau n " +
            "JOIN n.filiere f " +
            "JOIN f.ecole e " +
            "WHERE e.institut.id = :institutId")
    List<AssistantPedagogique> findByInstitutId(@Param("institutId") Long institutId);

    /**
     * ✅ AJOUTÉ — Trouve les assistants affectés à une classe précise.
     * Utilisé pour vérifier les affectations après migration.
     */
    @Query("SELECT a FROM AssistantPedagogique a JOIN a.classes c WHERE c.id = :classeId")
    List<AssistantPedagogique> findByClasseId(@Param("classeId") Long classeId);

    /**
     * ✅ AJOUTÉ — Trouve les assistants d'une filière donnée.
     * Utilisé lors de la migration sélective par filière.
     */
    @Query("SELECT DISTINCT a FROM AssistantPedagogique a " +
            "JOIN a.classes c " +
            "JOIN c.niveau n " +
            "WHERE n.filiere.id = :filiereId")
    List<AssistantPedagogique> findByFiliereId(@Param("filiereId") Long filiereId);
}