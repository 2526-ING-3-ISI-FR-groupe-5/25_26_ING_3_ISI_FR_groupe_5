package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.PlageHoraire;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;


@Getter
@Setter
public class PlageHoraireRequest {

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "La programmation est obligatoire")
    private Long programmationUEId;

    @NotNull(message = "Le jour est obligatoire")
    private LocalDate jour;

    @NotNull(message = "L'heure de début est obligatoire")
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    private LocalTime heureFin;

    @NotBlank(message = "La salle est obligatoire")
    private String salle;

    private String couleur;
}