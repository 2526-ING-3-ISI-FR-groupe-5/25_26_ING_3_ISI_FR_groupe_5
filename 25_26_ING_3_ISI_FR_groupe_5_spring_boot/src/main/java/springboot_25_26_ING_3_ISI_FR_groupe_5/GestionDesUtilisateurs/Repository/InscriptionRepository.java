package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.List;
import java.util.Optional;

@Repository
public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    // Recherches de base
    List<Inscription> findByAnneeAcademiqueId(Long anneeId);
    List<Inscription> findByEtudiantId(Long etudiantId);
    List<Inscription> findByClasseId(Long classeId);

    Optional<Inscription> findByEtudiantIdAndAnneeAcademiqueId(Long etudiantId, Long anneeId);
    List<Inscription> findByClasseIdAndAnneeAcademiqueId(Long classeId, Long anneeId);

    boolean existsByEtudiantIdAndAnneeAcademiqueId(Long etudiantId, Long anneeId);

    // Recherches par décision
    List<Inscription> findByAnneeAcademiqueIdAndDecisionFinAnnee(Long anneeId, String decision);

    @Query("SELECT i FROM Inscription i WHERE i.anneeAcademique.id = :anneeId AND i.decisionFinAnnee = :decision")
    List<Inscription> findByAnneeAndDecision(@Param("anneeId") Long anneeId, @Param("decision") String decision);

    // Recherches par statut
    List<Inscription> findByAnneeAcademiqueIdAndStatut(Long anneeId, StatutInscription statut);

    // ✅ Méthode pour les étudiants actifs d'une classe
    List<Inscription> findByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);

    // Compteurs
    long countByClasseIdAndAnneeAcademiqueIdAndStatut(Long classeId, Long anneeId, StatutInscription statut);
}