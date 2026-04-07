package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PlageHoraireRepository extends JpaRepository<PlageHoraire, Long> {

    // ──────────────────────────────────────────────
    // PAR SEMESTRE
    // ──────────────────────────────────────────────
    List<PlageHoraire> findBySemestreId(Long semestreId);

    @Query("SELECT p FROM PlageHoraire p WHERE p.semestre.actif = true AND p.semestre.anneeAcademique.active = true")
    List<PlageHoraire> findPlagesSemestreActif();

    // ──────────────────────────────────────────────
    // PAR CLASSE
    // ──────────────────────────────────────────────
    List<PlageHoraire> findByClasseId(Long classeId);

    List<PlageHoraire> findByClasseIdAndSemestreId(Long classeId, Long semestreId);

    List<PlageHoraire> findByClasseIdAndJourBetween(Long classeId, LocalDate debut, LocalDate fin);

    // ──────────────────────────────────────────────
    // PAR ENSEIGNANT (via programmationUE)
    // ──────────────────────────────────────────────
    @Query("SELECT p FROM PlageHoraire p JOIN p.programmationUE pu JOIN pu.enseignants e WHERE e.id = :enseignantId")
    List<PlageHoraire> findByEnseignantId(@Param("enseignantId") Long enseignantId);

    @Query("SELECT p FROM PlageHoraire p JOIN p.programmationUE pu JOIN pu.enseignants e WHERE e.id = :enseignantId AND p.semestre.id = :semestreId")
    List<PlageHoraire> findByEnseignantIdAndSemestreId(@Param("enseignantId") Long enseignantId, @Param("semestreId") Long semestreId);

    @Query("SELECT p FROM PlageHoraire p JOIN p.programmationUE pu JOIN pu.enseignants e WHERE e.id = :enseignantId AND p.jour BETWEEN :debut AND :fin")
    List<PlageHoraire> findByEnseignantAndJourBetween(@Param("enseignantId") Long enseignantId, @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    List<PlageHoraire> findByProgrammationUEEnseignantsId(Long enseignantId);

    // ──────────────────────────────────────────────
    // PAR UE (via programmationUE)
    // ──────────────────────────────────────────────
    @Query("SELECT p FROM PlageHoraire p WHERE p.programmationUE.ue.id = :ueId")
    List<PlageHoraire> findByUeId(@Param("ueId") Long ueId);

    List<PlageHoraire> findByProgrammationUEUeId(Long ueId);

    // ──────────────────────────────────────────────
    // PAR PROGRAMMATION UE
    // ──────────────────────────────────────────────
    List<PlageHoraire> findByProgrammationUESemestreId(Long semestreId);

    void deleteByProgrammationUEId(Long programmationUEId);

    // ──────────────────────────────────────────────
    // PAR ANNÉE ACADÉMIQUE
    // ──────────────────────────────────────────────
    @Query("SELECT p FROM PlageHoraire p WHERE p.semestre.anneeAcademique.id = :anneeId")
    List<PlageHoraire> findByAnnee(@Param("anneeId") Long anneeId);

    // ──────────────────────────────────────────────
    // CONFLITS (CRÉATION)
    // ──────────────────────────────────────────────
    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p WHERE p.classe.id = :classeId AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitClasse(@Param("classeId") Long classeId, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin);

    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p WHERE p.salle = :salle AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitSalle(@Param("salle") String salle, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin);

    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p JOIN p.programmationUE pu JOIN pu.enseignants e WHERE e.id = :enseignantId AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitEnseignant(@Param("enseignantId") Long enseignantId, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin);

    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p WHERE p.programmationUE.ue.id = :ueId AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitUe(@Param("ueId") Long ueId, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin);

    // ──────────────────────────────────────────────
    // CONFLITS (MODIFICATION - exclut la séance en cours)
    // ──────────────────────────────────────────────
    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p WHERE p.classe.id = :classeId AND p.id <> :id AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitClasseSaufId(@Param("classeId") Long classeId, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin, @Param("id") Long id);

    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p WHERE p.salle = :salle AND p.id <> :id AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitSalleSaufId(@Param("salle") String salle, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin, @Param("id") Long id);

    @Query("SELECT COUNT(p) > 0 FROM PlageHoraire p JOIN p.programmationUE pu JOIN pu.enseignants e WHERE e.id = :enseignantId AND p.id <> :id AND p.jour = :jour AND p.heureDebut < :heureFin AND p.heureFin > :heureDebut")
    boolean existsConflitEnseignantSaufId(@Param("enseignantId") Long enseignantId, @Param("jour") LocalDate jour, @Param("heureDebut") LocalTime heureDebut, @Param("heureFin") LocalTime heureFin, @Param("id") Long id);

    // ──────────────────────────────────────────────
    // CRÉNEAUX OCCUPÉS
    // ──────────────────────────────────────────────
    @Query("SELECT p.jour, p.heureDebut, p.heureFin FROM PlageHoraire p WHERE p.classe.id = :classeId AND p.semestre.id = :semestreId")
    List<Object[]> findCreneauxOccupes(@Param("classeId") Long classeId, @Param("semestreId") Long semestreId);
}