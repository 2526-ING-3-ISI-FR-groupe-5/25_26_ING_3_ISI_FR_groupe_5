package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutPresence;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JustificatifRepository extends JpaRepository<Justificatif, Long> {


    List<Justificatif> findByEtudiantIdOrderByDateSoumissionDesc(Long etudiantId);

    List<Justificatif> findByEtudiantId(Long etudiantId);


    @Query("""
        SELECT j FROM Justificatif j
        WHERE j.etudiant.classe.id = :classeId
        AND j.statut = 'EN_ATTENTE'
        ORDER BY j.createdAt ASC
        """)
    List<Justificatif> findEnAttenteByClasse(@Param("classeId") Long classeId);

    @Query("""
        SELECT j FROM Justificatif j
        WHERE j.etudiant.institut.id = :institutId
        AND j.statut = 'EN_ATTENTE'
        ORDER BY j.createdAt ASC
        """)
    List<Justificatif> findEnAttenteByInstitut(@Param("institutId") Long institutId);

    List<Justificatif> findByEtudiantIdOrderByCreatedAtDesc(Long etudiantId);

    boolean existsByEtudiantIdAndDateDebutAbsence(@NotNull(message = "L'étudiant est obligatoire") Long etudiantId, LocalDateTime dateDebutAbsence);


    // Nécessaire pour getRetardsByPlage()


    // ✅ Vérification doublon
    boolean existsByEtudiantIdAndDateDebutAbsence(
            Long etudiantId,
            LocalDate dateDebutAbsence
    );

}