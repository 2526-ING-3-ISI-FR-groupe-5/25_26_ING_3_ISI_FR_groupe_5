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
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;

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

    // UtilisateurRepository.java
    @Query("""
    SELECT DISTINCT u FROM Utilisateur u
    LEFT JOIN FETCH u.roles r
    LEFT JOIN FETCH r.permissions
    WHERE u.email = :email AND u.active = true
""")
    Optional<Utilisateur> findByEmailWithRoles(@Param("email") String email);

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Utilisateur> findByEmailAndActiveTrue(String email);

    @Query("SELECT u FROM Utilisateur u JOIN u.roles r WHERE r.nom = :roleNom")
    List<Utilisateur> findByRole(@Param("roleNom") String roleNom);

    // 🆕 Pour le multi-instituts
    Page<Utilisateur> findByInstitutId(Long institutId, Pageable pageable);

    // 🆕 Recherche par institut
    @Query("""
        SELECT u FROM Utilisateur u 
        WHERE (:institutId IS NULL OR u.institut.id = :institutId)
        AND (:active IS NULL OR u.active = :active)
    """)
    Page<Utilisateur> findAllFiltre(
            @Param("institutId") Long institutId,
            @Param("active") Boolean active,
            Pageable pageable);

    // 🆕 Recherche globale avec filtre institut
    @Query("""
        SELECT u FROM Utilisateur u 
        WHERE (:institutId IS NULL OR u.institut.id = :institutId)
        AND (LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) 
             OR LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%'))
             OR LOWER(u.email) LIKE LOWER(CONCAT('%', :recherche, '%')))
    """)
    Page<Utilisateur> search(
            @Param("institutId") Long institutId,
            @Param("recherche") String recherche,
            Pageable pageable);

    @Query(value = """
    SELECT u FROM Utilisateur u
    WHERE TYPE(u) IN (Enseignant, AssistantPedagogique, Surveillant)
    AND (:institutId IS NULL OR u.institut.id = :institutId)
    AND (:type = 'TOUS' OR
        (:type = 'ENS' AND TYPE(u) = Enseignant) OR
        (:type = 'AST' AND TYPE(u) = AssistantPedagogique) OR
        (:type = 'SUR' AND TYPE(u) = Surveillant))
    AND (:recherche IS NULL OR :recherche = '' OR
        LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
        LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')))
    AND (:anneeId IS NULL
        OR TYPE(u) = AssistantPedagogique
        OR TYPE(u) = Surveillant
        OR (TYPE(u) = Enseignant AND EXISTS (
            SELECT p FROM ProgrammationUE p JOIN p.enseignants ens
            WHERE ens = u AND p.semestre.anneeAcademique.id = :anneeId)))
    """,
    countQuery = """
    SELECT COUNT(u) FROM Utilisateur u
    WHERE TYPE(u) IN (Enseignant, AssistantPedagogique, Surveillant)
    AND (:institutId IS NULL OR u.institut.id = :institutId)
    AND (:type = 'TOUS' OR
        (:type = 'ENS' AND TYPE(u) = Enseignant) OR
        (:type = 'AST' AND TYPE(u) = AssistantPedagogique) OR
        (:type = 'SUR' AND TYPE(u) = Surveillant))
    AND (:recherche IS NULL OR :recherche = '' OR
        LOWER(u.nom) LIKE LOWER(CONCAT('%', :recherche, '%')) OR
        LOWER(u.prenom) LIKE LOWER(CONCAT('%', :recherche, '%')))
    AND (:anneeId IS NULL
        OR TYPE(u) = AssistantPedagogique
        OR TYPE(u) = Surveillant
        OR (TYPE(u) = Enseignant AND EXISTS (
            SELECT p FROM ProgrammationUE p JOIN p.enseignants ens
            WHERE ens = u AND p.semestre.anneeAcademique.id = :anneeId)))
    """)
    Page<Utilisateur> searchWithFilters(
            @Param("recherche") String recherche,
            @Param("type") String type,
            @Param("institutId") Long institutId,
            @Param("anneeId") Long anneeId,
            Pageable pageable
    );
    List<Utilisateur> findByInstitutId(Long institutId);

    @Query("""
        SELECT u FROM Utilisateur u
        WHERE TYPE(u) IN (Enseignant, AssistantPedagogique, Surveillant)
        AND (:institutId IS NULL OR u.institut.id = :institutId)
        ORDER BY u.nom ASC
    """)
    List<Utilisateur> findPersonnelByInstitutId(@Param("institutId") Long institutId);

    @Query("""
        SELECT COUNT(u) FROM Utilisateur u
        WHERE TYPE(u) IN (Enseignant, AssistantPedagogique, Surveillant)
        AND (:institutId IS NULL OR u.institut.id = :institutId)
    """)
    long countPersonnelByInstitut(@Param("institutId") Long institutId);
}