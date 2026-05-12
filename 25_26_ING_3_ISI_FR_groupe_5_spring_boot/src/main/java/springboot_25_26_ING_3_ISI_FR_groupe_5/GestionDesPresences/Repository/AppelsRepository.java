package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Enum.StatutPresence;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppelsRepository extends JpaRepository<Appels, Long> {

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR ÉTUDIANT
    // ═══════════════════════════════════════════════════════════

    List<Appels> findByEtudiantId(Long etudiantId);

    List<Appels> findBySessionAppelId(Long sessionId);

    List<Appels> findByPlageHoraireIdAndStatut(Long plageHoraireId, StatutPresence statut);

    Optional<Appels> findByEtudiantIdAndPlageHoraireId(Long etudiantId, Long plageHoraireId);

    List<Appels> findBySessionAppelIdAndStatut(Long sessionId, StatutPresence statut);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR PLAGE HORAIRE
    // ═══════════════════════════════════════════════════════════

    List<Appels> findByPlageHoraireId(Long plageHoraireId);

    // ═══════════════════════════════════════════════════════════
    // RECHERCHES PAR ENSEIGNANT
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT a FROM Appels a JOIN FETCH a.plageHoraire WHERE a.enseignant.id = :enseignantId ORDER BY a.dateValidation DESC")
    Page<Appels> findByEnseignantIdWithPlage(@Param("enseignantId") Long enseignantId, Pageable pageable);

    // ✅ CORRIGÉ : Suppression du doublon - cette méthode était déjà définie plus haut (ligne ~37)
    // List<Appels> findByEtudiantIdAndStatut(Long etudiantId, StatutPresence statut); ← SUPPRIMÉ

    // ═══════════════════════════════════════════════════════════
    // STATISTIQUES
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT COUNT(a) FROM Appels a WHERE a.etudiant.id = :etudiantId AND a.present = false")
    long countAbsencesByEtudiant(@Param("etudiantId") Long etudiantId);

    // ═══════════════════════════════════════════════════════════
    // MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT a FROM Appels a WHERE a.etudiant.institut.id = :institutId ORDER BY a.dateValidation DESC")
    Page<Appels> findByInstitutId(@Param("institutId") Long institutId, Pageable pageable);

    @Query("""
        SELECT a FROM Appels a
        WHERE a.etudiant.institut.id = :institutId
        AND a.present = false
        AND a.justificatif IS NULL
        ORDER BY a.dateValidation DESC
    """)
    Page<Appels> findAbsencesNonJustifieesByInstitutId(@Param("institutId") Long institutId, Pageable pageable);

    // ═══════════════════════════════════════════════════════════
    // VÉRIFICATIONS
    // ═══════════════════════════════════════════════════════════

    @Query("SELECT a FROM Appels a JOIN FETCH a.etudiant JOIN FETCH a.plageHoraire WHERE a.id = :id")
    Appels findByIdWithDetails(@Param("id") Long id);

    // ✅ Mise à jour bulk — une seule requête au lieu de N
    @Modifying
    @Query("""
        UPDATE Appels a
        SET a.statut = :statut
        WHERE a.justificatif.id = :justificatifId
    """)
    void updateStatutByJustificatifId(
            @Param("justificatifId") Long justificatifId,
            @Param("statut") StatutPresence statut
    );

    // ✅ CORRIGÉ : Ajout des @Param manquants
    @Query("""
        SELECT COUNT(a) FROM Appels a 
        WHERE a.etudiant.id = :etudiantId 
        AND a.plageHoraire.semestre.actif = true 
        AND a.statut = :statut
    """)
    long countByEtudiantAndStatutInActiveSemestre(
            @Param("etudiantId") Long etudiantId,
            @Param("statut") StatutPresence statut
    );

    @Query("""
        SELECT COUNT(a) FROM Appels a 
        WHERE a.etudiant.id = :etudiantId 
        AND a.plageHoraire.semestre.actif = true 
        AND a.statut = :statut
    """)
    long countByEtudiantAndStatutAndSemestreActif(
            @Param("etudiantId") Long etudiantId,
            @Param("statut") StatutPresence statut
    );

    // Pour les absences non justifiées (NJ)
    @Query("""
        SELECT COUNT(a) FROM Appels a 
        WHERE a.etudiant.id = :etudiantId 
        AND a.plageHoraire.semestre.actif = true 
        AND a.statut = 'ABSENT' 
        AND a.justificatif IS NULL
    """)
    long countAbsencesNonJustifieesByEtudiant(@Param("etudiantId") Long etudiantId);

    List<Appels> findByEtudiantIdAndStatut(Long etudiantId, StatutPresence statutPresence);
}