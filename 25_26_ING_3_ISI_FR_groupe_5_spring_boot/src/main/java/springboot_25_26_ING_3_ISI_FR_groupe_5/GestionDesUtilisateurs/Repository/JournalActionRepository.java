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

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES SIMPLES
    // ═══════════════════════════════════════════════════════════

    Page<JournalAction> findByUtilisateurId(Long utilisateurId, Pageable pageable);
    Page<JournalAction> findByTypeAction(TypeAction typeAction, Pageable pageable);
    Page<JournalAction> findByStatut(StatutAction statut, Pageable pageable);
    Page<JournalAction> findByEntiteConcernee(String entiteConcernee, Pageable pageable);

    // ✅ Méthode paginée pour entité + ID
    Page<JournalAction> findByEntiteConcerneeAndEntiteId(String entiteConcernee, Long entiteId, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR PÉRIODE
    // ═══════════════════════════════════════════════════════════

    Page<JournalAction> findByDateActionBetween(LocalDateTime debut, LocalDateTime fin, Pageable pageable);
    Page<JournalAction> findByUtilisateurIdAndDateActionBetween(Long utilisateurId, LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES COMBINÉES
    // ═══════════════════════════════════════════════════════════

    Page<JournalAction> findByUtilisateurIdAndTypeAction(Long utilisateurId, TypeAction typeAction, Pageable pageable);
    Page<JournalAction> findByUtilisateurIdAndStatut(Long utilisateurId, StatutAction statut, Pageable pageable);
    Page<JournalAction> findByTypeActionAndStatut(TypeAction typeAction, StatutAction statut, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR INSTITUT
    // ═══════════════════════════════════════════════════════════

    Page<JournalAction> findByInstitutId(Long institutId, Pageable pageable);
    Page<JournalAction> findByInstitutIdAndTypeAction(Long institutId, TypeAction typeAction, Pageable pageable);
    Page<JournalAction> findByInstitutIdAndStatut(Long institutId, StatutAction statut, Pageable pageable);
    Page<JournalAction> findByInstitutIdAndDateActionBetween(Long institutId, LocalDateTime debut, LocalDateTime fin, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHE AVANCÉE (avec filtres optionnels)
    // ═══════════════════════════════════════════════════════════

    @Query("""
        SELECT j FROM JournalAction j
        WHERE (:institutId IS NULL OR j.institut.id = :institutId)
        AND (:utilisateurId IS NULL OR j.utilisateur.id = :utilisateurId)
        AND (:typeAction IS NULL OR j.typeAction = :typeAction)
        AND (:statut IS NULL OR j.statut = :statut)
        AND (:debut IS NULL OR j.dateAction >= :debut)
        AND (:fin IS NULL OR j.dateAction <= :fin)
        ORDER BY j.dateAction DESC
    """)
    Page<JournalAction> search(
            @Param("institutId") Long institutId,
            @Param("utilisateurId") Long utilisateurId,
            @Param("typeAction") TypeAction typeAction,
            @Param("statut") StatutAction statut,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin,
            Pageable pageable
    );

    // ═══════════════════════════════════════════════════════════
    // 🆕 MÉTHODE CRITIQUE CORRIGÉE : Historique par entité (List)
    // ═══════════════════════════════════════════════════════════

    /**
     * Récupère l'historique des actions pour une entité donnée (ex: "Inscription", ID: 123)
     * ✅ Correction : j.entiteConcernee (et NON j.entiteType qui n'existe pas)
     */
    @Query("""
        SELECT j FROM JournalAction j 
        WHERE j.entiteConcernee = :entiteConcernee 
        AND j.entiteId = :entiteId 
        ORDER BY j.dateAction DESC
    """)
    List<JournalAction> findByEntiteConcerneeAndEntiteIdOrderByDateDesc(
            @Param("entiteConcernee") String entiteConcernee,
            @Param("entiteId") Long entiteId
    );

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT j.typeAction, COUNT(j) FROM JournalAction j GROUP BY j.typeAction ORDER BY COUNT(j) DESC")
    List<Object[]> countByTypeAction();

    @Query("""
        SELECT j.typeAction, COUNT(j) FROM JournalAction j 
        WHERE j.institut.id = :institutId 
        GROUP BY j.typeAction ORDER BY COUNT(j) DESC
    """)
    List<Object[]> countByTypeActionAndInstitut(@Param("institutId") Long institutId);

    @Query("""
        SELECT j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom, COUNT(j) 
        FROM JournalAction j 
        WHERE j.statut = 'ECHEC' 
        GROUP BY j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom 
        ORDER BY COUNT(j) DESC
    """)
    List<Object[]> countEchecByUtilisateur();

    @Query("""
        SELECT j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom, COUNT(j) 
        FROM JournalAction j 
        WHERE j.statut = 'ECHEC' AND j.institut.id = :institutId 
        GROUP BY j.utilisateur.id, j.utilisateur.nom, j.utilisateur.prenom 
        ORDER BY COUNT(j) DESC
    """)
    List<Object[]> countEchecByUtilisateurAndInstitut(@Param("institutId") Long institutId);

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

    // ═══════════════════════════════════════════════════════════
    // NETTOYAGE (optionnel)
    // ═══════════════════════════════════════════════════════════

    void deleteByDateActionBefore(LocalDateTime date);
}