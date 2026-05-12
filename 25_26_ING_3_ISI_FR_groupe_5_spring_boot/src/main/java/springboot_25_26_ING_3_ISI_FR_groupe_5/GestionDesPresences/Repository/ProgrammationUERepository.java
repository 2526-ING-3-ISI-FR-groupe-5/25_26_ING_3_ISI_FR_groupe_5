package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;

import java.util.List;

@Repository
public interface ProgrammationUERepository extends JpaRepository<ProgrammationUE, Long> {

    // ══════════════════════════════════════════
    // 🆕 PAR CLASSE + ANNÉE ACTIVE
    // ══════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.active = true
        ORDER BY p.ue.nom ASC
        """)
    List<ProgrammationUE> findByClasseIdAndAnneeActive(@Param("classeId") Long classeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        ORDER BY p.ue.nom ASC
        """)
    List<ProgrammationUE> findByClasseId(@Param("classeId") Long classeId);

    // ══════════════════════════════════════════
    // PAR CLASSE + ANNÉE
    // ══════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.semestre.anneeAcademique.id = :anneeId
        """)
    List<ProgrammationUE> findByAnneeAcademiqueId(@Param("anneeId") Long anneeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.classe.id = :classeId
        AND p.semestre.anneeAcademique.id = :anneeId
        """)
    List<ProgrammationUE> findByClasseAndAnnee(
            @Param("classeId") Long classeId,
            @Param("anneeId") Long anneeId);

    // ══════════════════════════════════════════
    // PAR ENSEIGNANT
    // ══════════════════════════════════════════

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

    // Vérifier si une UE est déjà programmée dans une classe pour un semestre
    boolean existsByUeIdAndClasseIdAndSemestreId(
            Long ueId, Long classeId, Long semestreId
    );

    List<ProgrammationUE> findBySemestre_AnneeAcademique_Id(Long anneeId);

    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
    """)
    List<ProgrammationUE> findByEnseignantId(@Param("enseignantId") Long enseignantId);

    @Query("""
        SELECT DISTINCT p.classe FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.anneeAcademique.active = true
    """)
    List<Classe> findClassesByEnseignantId(@Param("enseignantId") Long enseignantId);

    @Query("""
        SELECT DISTINCT p FROM ProgrammationUE p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.id = :semestreId
        """)
    List<ProgrammationUE> findByEnseignantIdAndSemestreId(
            @Param("enseignantId") Long enseignantId,
            @Param("semestreId") Long semestreId);

    // ══════════════════════════════════════════
    // PAR UE
    // ══════════════════════════════════════════

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.ue.id = :ueId
        AND p.semestre.anneeAcademique.id = :anneeId
        """)
    List<ProgrammationUE> findByUeAndAnnee(
            @Param("ueId") Long ueId,
            @Param("anneeId") Long anneeId);

    @Query("""
        SELECT p FROM ProgrammationUE p
        WHERE p.ue.id = :ueId
        AND p.semestre.id = :semestreId
        """)
    List<ProgrammationUE> findByUeIdAndSemestreId(
            @Param("ueId") Long ueId,
            @Param("semestreId") Long semestreId);

    // ══════════════════════════════════════════
    // PAR SEMESTRE
    // ══════════════════════════════════════════

    List<ProgrammationUE> findBySemestreId(Long semestreId);

    @Query("""
        SELECT p FROM ProgrammationUE p 
        JOIN p.enseignants e 
        WHERE e.id = :enseignantId 
        AND p.semestre.actif = true
    """)
    List<ProgrammationUE> findByEnseignantsIdAndSemestreActifTrue(
            @Param("enseignantId") Long enseignantId
    );

    // ✅ CORRIGÉ : Suppression de la méthode findCoursEnseignantAujourdhui
    // qui retournait des PlageHoraire au lieu de ProgrammationUE
    // Cette méthode existe déjà dans PlageHoraireRepository
}