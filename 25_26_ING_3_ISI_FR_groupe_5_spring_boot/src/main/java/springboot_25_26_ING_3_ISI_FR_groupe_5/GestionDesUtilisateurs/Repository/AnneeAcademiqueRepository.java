package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnneeAcademiqueRepository extends JpaRepository<Annee_academique, Long> {

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES GLOBALES (pour SUPER_ADMIN ou compatibilité)
    // ═══════════════════════════════════════════════════════════

    Optional<Annee_academique> findByActiveTrue();

    boolean existsByNom(String nom);

    Optional<Annee_academique> findByNom(String nom);

    List<Annee_academique> findAllByOrderByNomDesc();

    // ═══════════════════════════════════════════════════════════
    // MÉTHODES MULTI-INSTITUTS
    // ═══════════════════════════════════════════════════════════

    // ✅ Correction : retourner Optional<Annee_academique> et non Optional<Object>
    Optional<Annee_academique> findByInstitutIdAndActiveTrue(Long institutId);

    Optional<Annee_academique> findByNomAndInstitutId(String nom, Long institutId);

    boolean existsByNomAndInstitutId(String nom, Long institutId);

    List<Annee_academique> findByInstitutId(Long institutId);

    List<Annee_academique> findByInstitutIdOrderByNomDesc(Long institutId);
}