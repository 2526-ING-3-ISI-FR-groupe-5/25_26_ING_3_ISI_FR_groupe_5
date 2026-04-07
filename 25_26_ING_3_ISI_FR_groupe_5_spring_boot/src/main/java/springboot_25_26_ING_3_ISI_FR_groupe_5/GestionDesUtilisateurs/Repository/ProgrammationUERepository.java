package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.util.List;

@Repository
public interface ProgrammationUERepository extends JpaRepository<ProgrammationUE, Long> {

    // Programmations d'une classe pour une année
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    List<ProgrammationUE> findByClasseAndAnnee(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId
    );

    // Programmations d'un enseignant pour une année
    @Query("""
        SELECT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    List<ProgrammationUE> findByEnseignantAndAnnee(
            @Param("enseignantId") Long enseignantId,
            @Param("anneeId") Long anneeId
    );

    // Programmations d'une UE pour une année
    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.ue.id = :ueId
        AND p.semestre.anneeAcademique.id = :anneeId
    """)
    List<ProgrammationUE> findByUeAndAnnee(
            @Param("ueId") Long ueId,
            @Param("anneeId") Long anneeId
    );

    // Vérifier si une UE est déjà programmée dans une classe pour un semestre
    boolean existsByUeIdAndClasseIdAndSemestreId(
            Long ueId, Long classeId, Long semestreId
    );

    // ✅ Ajouter dans ProgrammationUERepository
    List<ProgrammationUE> findBySemestreId(Long semestreId);
    // Ajouter cette méthode
    List<ProgrammationUE> findBySemestre_AnneeAcademique_Id(Long anneeId);
}