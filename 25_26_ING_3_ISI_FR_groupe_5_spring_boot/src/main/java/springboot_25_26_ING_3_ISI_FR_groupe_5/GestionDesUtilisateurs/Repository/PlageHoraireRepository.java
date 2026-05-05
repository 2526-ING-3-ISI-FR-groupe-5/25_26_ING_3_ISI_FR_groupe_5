package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlageHoraireRepository
        extends JpaRepository<PlageHoraire, Long> {

    // ============================================
    // PAR SEMESTRE
    // ============================================

    List<PlageHoraire> findBySemestreId(Long semestreId);

    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.semestre.actif = true
        AND p.semestre.anneeAcademique.active = true
        ORDER BY p.jour, p.heureDebut
    """)
    List<PlageHoraire> findPlagesSemestreActif();

    // ============================================
    // PAR CLASSE
    // ============================================

    List<PlageHoraire> findByClasseId(Long classeId);

    List<PlageHoraire> findByClasseIdAndSemestreId(
            Long classeId, Long semestreId);

    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.jour BETWEEN :debut AND :fin
        ORDER BY p.jour, p.heureDebut
    """)
    List<PlageHoraire> findByClasseAndSemaine(
            @Param("classeId") Long classeId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.jour = :jour
        ORDER BY p.heureDebut
    """)
    List<PlageHoraire> findByClasseAndJour(
            @Param("classeId") Long classeId,
            @Param("jour") LocalDate jour
    );

    // ============================================
    // PAR ENSEIGNANT
    // ✅ Joint sur p.enseignants (relation directe)
    // ============================================

    @Query("""
        SELECT p FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        ORDER BY p.jour DESC, p.heureDebut DESC
    """)
    List<PlageHoraire> findByEnseignantId(
            @Param("enseignantId") Long enseignantId
    );

    @Query("""
        SELECT p FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.semestre.id = :semestreId
        ORDER BY p.jour, p.heureDebut
    """)
    List<PlageHoraire> findByEnseignantIdAndSemestreId(
            @Param("enseignantId") Long enseignantId,
            @Param("semestreId") Long semestreId
    );

    @Query("""
        SELECT p FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.jour BETWEEN :debut AND :fin
        ORDER BY p.jour, p.heureDebut
    """)
    List<PlageHoraire> findByEnseignantAndJourBetween(
            @Param("enseignantId") Long enseignantId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin
    );

    // ============================================
    // PAR UE
    // ============================================

    List<PlageHoraire> findByProgrammationUEUeId(Long ueId);

    // ============================================
    // PAR PROGRAMMATION UE
    // ============================================

    List<PlageHoraire> findByProgrammationUESemestreId(Long semestreId);
    List<PlageHoraire> findByProgrammationUEId(Long programmationUEId);

    void deleteByProgrammationUEId(Long programmationUEId);

    // ============================================
    // PAR ANNÉE ACADÉMIQUE
    // ============================================

    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.semestre.anneeAcademique.id = :anneeId
        ORDER BY p.jour, p.heureDebut
    """)
    List<PlageHoraire> findByAnnee(@Param("anneeId") Long anneeId);

    // ============================================
    // SYSTÈME D'APPEL
    // ============================================

    // Cours du jour pour une classe
    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.jour = :aujourd_hui
        AND p.typeSeance NOT IN ('PAUSE', 'EVENEMENT')
        ORDER BY p.heureDebut
    """)
    List<PlageHoraire> findCoursAujourdhui(
            @Param("classeId") Long classeId,
            @Param("aujourd_hui") LocalDate aujourdhui
    );

    // Cours actuellement en cours (heure actuelle entre début et fin)
    @Query("""
        SELECT p FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.jour = :aujourd_hui
        AND p.heureDebut <= :maintenant
        AND p.heureFin > :maintenant
        AND p.typeSeance NOT IN ('PAUSE', 'EVENEMENT')
    """)
    Optional<PlageHoraire> findCoursEnCours(
            @Param("classeId") Long classeId,
            @Param("aujourd_hui") LocalDate aujourdhui,
            @Param("maintenant") LocalTime maintenant
    );

    // Cours de l'enseignant du jour
    @Query("""
        SELECT p FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.jour = :aujourd_hui
        AND p.typeSeance NOT IN ('PAUSE', 'EVENEMENT')
        ORDER BY p.heureDebut
    """)
    List<PlageHoraire> findCoursEnseignantAujourdhui(
            @Param("enseignantId") Long enseignantId,
            @Param("aujourd_hui") LocalDate aujourdhui
    );

    // ============================================
    // CONFLITS — CRÉATION
    // ✅ Corrigés pour joindre sur p.enseignants
    // ============================================

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitClasse(
            @Param("classeId") Long classeId,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        WHERE p.salle = :salle
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitSalle(
            @Param("salle") String salle,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    // ✅ Joint sur p.enseignants — pas programmationUE.enseignants
    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitEnseignant(
            @Param("enseignantId") Long enseignantId,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        WHERE p.programmationUE.ue.id = :ueId
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitUe(
            @Param("ueId") Long ueId,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin
    );

    // ============================================
    // CONFLITS — MODIFICATION (exclut la séance en cours)
    // ============================================

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.id <> :id
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitClasseSaufId(
            @Param("classeId") Long classeId,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("id") Long id
    );

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        WHERE p.salle = :salle
        AND p.id <> :id
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitSalleSaufId(
            @Param("salle") String salle,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("id") Long id
    );

    @Query("""
        SELECT COUNT(p) > 0 FROM PlageHoraire p
        JOIN p.enseignants e
        WHERE e.id = :enseignantId
        AND p.id <> :id
        AND p.jour = :jour
        AND p.heureDebut < :heureFin
        AND p.heureFin > :heureDebut
    """)
    boolean existsConflitEnseignantSaufId(
            @Param("enseignantId") Long enseignantId,
            @Param("jour") LocalDate jour,
            @Param("heureDebut") LocalTime heureDebut,
            @Param("heureFin") LocalTime heureFin,
            @Param("id") Long id
    );

    // ============================================
    // CRÉNEAUX OCCUPÉS
    // ============================================

    @Query("""
        SELECT p.jour, p.heureDebut, p.heureFin
        FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.semestre.id = :semestreId
    """)
    List<Object[]> findCreneauxOccupes(
            @Param("classeId") Long classeId,
            @Param("semestreId") Long semestreId
    );

    // ============================================
    // STATISTIQUES
    // ============================================

    @Query("""
        SELECT COUNT(p) FROM PlageHoraire p
        WHERE p.classe.id = :classeId
        AND p.semestre.id = :semestreId
        AND p.typeSeance NOT IN ('PAUSE', 'EVENEMENT')
    """)
    Long countCoursByClasseAndSemestre(
            @Param("classeId") Long classeId,
            @Param("semestreId") Long semestreId
    );

    // ✅ Récupérer les plages et calculer en Java
    @Query("""
    SELECT p FROM PlageHoraire p
    WHERE p.classe.id = :classeId
    AND p.semestre.id = :semestreId
    AND p.typeSeance NOT IN ('PAUSE', 'EVENEMENT')
""")
    List<PlageHoraire> findCoursByClasseAndSemestre(
            @Param("classeId") Long classeId,
            @Param("semestreId") Long semestreId
    );
}