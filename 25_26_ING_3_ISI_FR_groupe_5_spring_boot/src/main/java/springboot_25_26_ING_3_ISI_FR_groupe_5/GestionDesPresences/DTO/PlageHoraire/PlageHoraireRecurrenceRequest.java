package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Semestre;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;

@Getter
@Setter
public class PlageHoraireRecurrenceRequest {

    /**
     * Remplace ueId — on passe par ProgrammationUE qui contient
     * déjà l'UE, le semestre, la classe et les enseignants assignés.
     */
    @NotNull(message = "La programmation UE est obligatoire")
    private Long programmationUEId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;

    /** Enseignant ponctuel — si null, ceux de la ProgrammationUE sont utilisés. */
    private Long enseignantId;

    @NotNull(message = "Les jours de la semaine sont obligatoires")
    private Set<DayOfWeek> jours;

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;
}