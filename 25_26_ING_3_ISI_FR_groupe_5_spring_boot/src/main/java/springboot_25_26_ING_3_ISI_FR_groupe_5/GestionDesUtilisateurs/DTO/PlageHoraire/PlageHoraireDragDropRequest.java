package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class PlageHoraireDragDropRequest {

    @NotNull(message = "L'UE est obligatoire")
    private Long ueId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "Le semestre est obligatoire")
    private Long semestreId;

    @NotNull(message = "Le jour est obligatoire")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate jour;

    @NotNull(message = "L'heure de début est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime heureFin;

    private String salle;
    private String couleur;
    private Long enseignantId;  // Optionnel, choisi dans la modale
}