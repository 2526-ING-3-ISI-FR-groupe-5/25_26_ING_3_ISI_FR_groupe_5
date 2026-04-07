package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscriptionRequest {

    @NotNull(message = "L'étudiant est obligatoire")
    private Long etudiantId;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;

    @NotNull(message = "L'année académique est obligatoire")
    private Long anneeAcademiqueId;
}