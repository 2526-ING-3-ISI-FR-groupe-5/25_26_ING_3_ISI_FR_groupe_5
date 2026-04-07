package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.semestre;

import CarnetRouge.CarnetRouge.GDU.Enum.TypeSemestre;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SemestreRequest {

    @NotNull(message = "Le type de semestre est obligatoire")
    private TypeSemestre typeSemestre;

    private boolean actif;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;
}