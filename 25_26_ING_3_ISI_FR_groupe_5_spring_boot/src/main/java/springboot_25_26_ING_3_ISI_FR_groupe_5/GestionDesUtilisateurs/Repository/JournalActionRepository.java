package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.JournalAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface JournalActionRepository extends JpaRepository<JournalAction, Long> {

    // ============================================
    // Recherches simples
    // ============================================

    Page<JournalAction> findByUtilisateurId(Long utilisateurId, Pageable pageable);
    Page<JournalAction> findByTypeAction(TypeAction typeAction, Pageable pageable);
    Page<JournalAction> findByStatut(StatutAction statut, Pageable pageable);
    Page<JournalAction> findByEntiteConcernee(String entiteConcernee, Pageable pageable);
    Page<JournalAction> findByEntiteConcerneeAndEntiteId(String entiteConcernee, Long entiteId, Pageable pageable);

    // ============================================
    // Recherches par période
    // ============================================

    Page<JournalAction> findByDateActionBetween(LocalDateTime debut, LocalDateTime fin, Pageable pageable);
    Page<JournalAction> findByUtilisateurIdAndDateActionBetween(Long utilisateurId, LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    // ============================================
    // Recherches combinées simples
    // ============================================

    Page<JournalAction> findByUtilisateurIdAndTypeAction(Long utilisateurId, TypeAction typeAction, Pageable pageable);
    Page<JournalAction> findByUtilisateurIdAndStatut(Long utilisateurId, StatutAction statut, Pageable pageable);
    Page<JournalAction> findByTypeActionAndStatut(TypeAction typeAction, StatutAction statut, Pageable pageable);

    // ============================================
    // Recherche avancée — TOUS les critères (corrigée)
    // ============================================

    @Query("""
        SELECT j FROM JournalAction j
        WHERE (COALESCE(:utilisateurId, -1) = -1 OR j.utilisateur.id = :utilisateurId)
        AND (COALESCE(:typeAction, '') = '' OR j.typeAction = :typeAction)
        AND (COALESCE(:statut, '') = '' OR j.statut = :statut)
        AND (COALESCE(:debut, NULL) IS NULL OR j.dateAction >= :debut)
        AND (COALESCE(:fin, NULL) IS NULL OR j.dateAction <= :fin)
        ORDER BY j.dateAction DESC
    """)
    Page<JournalAction> search(
            @Param("utilisateurId") Long utilisateurId,
            @Param("typeAction") TypeAction typeAction,
            @Param("statut") StatutAction statut,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            Pageable pageable
    );

    // ============================================
    // Statistiques
    // ============================================

    @Query("SELECT j.typeAction, COUNT(j) FROM JournalAction j GROUP BY j.typeAction ORDER BY COUNT(j) DESC")
    List<Object[]> countByTypeAction();

    @Query("""
        SELECT j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom, COUNT(j) 
        FROM JournalAction j 
        WHERE j.statut = 'ECHEC' 
        GROUP BY j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom 
        ORDER BY COUNT(j) DESC
    """)
    List<Object[]> countEchecByUtilisateur();

    @Query("""
        SELECT COUNT(j) FROM JournalAction j 
        WHERE j.typeAction = 'TENTATIVE_CONNEXION_ECHOUEE' 
        AND j.adresseIp = :ip 
        AND j.dateAction >= :depuis
    """)
    Long countTentativesEchouees(@Param("ip") String ip, @Param("depuis") LocalDateTime depuis);

    @Query("""
        SELECT j.adresseIp, COUNT(j) FROM JournalAction j 
        WHERE j.typeAction = 'TENTATIVE_CONNEXION_ECHOUEE' 
        AND j.dateAction >= :depuis 
        GROUP BY j.adresseIp 
        HAVING COUNT(j) >= :seuil 
        ORDER BY COUNT(j) DESC
    """)
    List<Object[]> findIpsSuspectes(@Param("depuis") LocalDateTime depuis, @Param("seuil") Long seuil);

    @Query("""
        SELECT COUNT(j) > 0 FROM JournalAction j 
        WHERE j.utilisateur.id = :utilisateurId 
        AND j.dateAction >= :depuis
    """)
    boolean isUtilisateurActifDepuis(@Param("utilisateurId") Long utilisateurId, @Param("depuis") LocalDateTime depuis);
}