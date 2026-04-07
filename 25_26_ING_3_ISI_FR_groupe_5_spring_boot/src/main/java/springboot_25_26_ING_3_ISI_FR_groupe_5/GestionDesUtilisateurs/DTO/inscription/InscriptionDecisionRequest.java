package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InscriptionDecisionRequest {

    @NotBlank(message = "La décision est obligatoire")
    private String decision; // ADMIS, REDOUBLANT, EXCLU, DIPLOME
}
