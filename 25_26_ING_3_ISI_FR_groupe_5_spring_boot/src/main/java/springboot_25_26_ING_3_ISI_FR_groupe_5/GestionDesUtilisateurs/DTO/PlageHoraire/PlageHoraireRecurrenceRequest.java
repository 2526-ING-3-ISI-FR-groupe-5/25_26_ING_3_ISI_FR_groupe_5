package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Getter
@Setter
public class PlageHoraireRecurrenceRequest {

    @NotNull(message = "La programmation UE est obligatoire")
    private Long programmationUEId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;

    @NotNull(message = "Les jours de la semaine sont obligatoires")
    private Set<DayOfWeek> jours;  // ex: [LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI]

    @NotNull(message = "La date de début est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateFin;

    private Long enseignantId;  // Optionnel
}