package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.inscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.DecisionFinAnnee;

@Getter
@Setter
public class InscriptionDecisionRequest {
    @NotNull(message = "La décision est obligatoire")
    private DecisionFinAnnee decision;

    private String observations;
}
