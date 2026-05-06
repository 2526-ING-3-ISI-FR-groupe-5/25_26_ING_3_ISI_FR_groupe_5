package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class PlageHoraireDragDropRequest {

    // ── Obligatoire ──
    private Long classeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate jour;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime heureDebut;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime heureFin;

    /**
     * Remplace ueId — ProgrammationUE contient déjà
     * l'UE, le semestre, la classe et les enseignants assignés.
     * Null uniquement pour PAUSE et EVENEMENT libres.
     */
    private Long programmationUEId;

    /**
     * Enseignant ponctuel (remplacement).
     * Si null → enseignants de la ProgrammationUE utilisés.
     */
    private Long enseignantId;

    // ── Optionnel ──
    private String salle;
    private String couleur;

    /**
     * CM | TD | TP | EVENEMENT | PAUSE — défaut : CM
     */
    private String typeSeance = "CM";

    /**
     * Titre libre — uniquement pour EVENEMENT et PAUSE.
     * Pour les cours, titre = programmationUE.ue.nom.
     */
    private String titre;

    /**
     * Jour de fin pour événements multi-jours / étirement horizontal.
     * Si null → même jour que `jour`.
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate jourFin;
}