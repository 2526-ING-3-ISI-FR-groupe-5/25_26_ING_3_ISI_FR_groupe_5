package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.evenement;

import CarnetRouge.CarnetRouge.GDU.Enum.TypeEvenement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EvenementRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    private String description;
    private String couleur;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull(message = "Le type est obligatoire")
    private TypeEvenement type;

    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId; // ✅ doit être ici
}