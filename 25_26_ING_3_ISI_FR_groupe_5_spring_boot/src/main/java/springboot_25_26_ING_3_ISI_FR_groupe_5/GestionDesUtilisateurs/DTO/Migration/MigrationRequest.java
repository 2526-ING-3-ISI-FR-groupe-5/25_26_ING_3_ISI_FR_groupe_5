package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MigrationRequest {

    @NotNull(message = "L'année cible est obligatoire")
    private Long nouvelleAnneeId;
}