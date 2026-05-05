package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.appel;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class AppelRetardRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "La plage horaire est obligatoire")
    private Long plageHoraireId;

    @NotNull(message = "L'enseignant est obligatoire")
    private Long enseignantId;

    /**
     * Heure d'arrivée réelle de l'étudiant.
     * Doit être après l'heure de début du cours.
     */
    @NotNull(message = "L'heure d'arrivée est obligatoire")
    private LocalTime heureArrivee;

    private String commentaire;
}