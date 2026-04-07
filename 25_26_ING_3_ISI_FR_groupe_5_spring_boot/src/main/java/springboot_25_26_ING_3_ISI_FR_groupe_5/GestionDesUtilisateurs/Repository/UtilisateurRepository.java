package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    @Query("SELECT DISTINCT u FROM Enseignant u " +
            "JOIN u.programmations p " +
            "JOIN p.semestre s " +
            "WHERE s.anneeAcademique.id = :anneeId " +
            "AND (:recherche IS NULL OR :recherche = '' OR " +
            "    LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "    LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR " +
            "    LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%')))")
    Page<Enseignant> searchEnseignantsByAnnee(
            @Param("anneeId") Long anneeId,
            @Param("recherche") String recherche,
            Pageable pageable
    );

    Optional<Utilisateur> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByEmailAndActiveTrue(String email);

    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.nom = :roleNom")
    List<Utilisateur> findByRole(@Param("roleName") String roleName);
}