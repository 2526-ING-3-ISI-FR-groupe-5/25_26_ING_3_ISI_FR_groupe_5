package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    @Query("SELECT u FROM Utilisateur u WHERE TYPE(u) IN (Enseignant, AssistantPedagogique , Surveillant ) " +
            "AND (:type = 'TOUS' OR " +
            "     (:type= 'SRV' AND TYPE(u)= Surveillant ) OR "+
            "    (:type = 'ENS' AND TYPE(u) = Enseignant) OR " +
            "    (:type = 'ASS' AND TYPE(u) = AssistantPedagogique )) " +
            "AND (:recherche IS NULL OR :recherche = '' OR " +
            "    LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "    LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')))")
    Page<Utilisateur> searchWithFilters(
            @Param("recherche") String recherche,
            @Param("type") String type,
            Pageable pageable
    );
}
