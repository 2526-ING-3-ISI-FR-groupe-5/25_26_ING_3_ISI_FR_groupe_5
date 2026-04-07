package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FiliereRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    private String description;

    @NotNull(message = "L'école est obligatoire")
    private Long ecoleId;

    @NotNull(message = "Le cycle est obligatoire")
    private Long cycleId;
}