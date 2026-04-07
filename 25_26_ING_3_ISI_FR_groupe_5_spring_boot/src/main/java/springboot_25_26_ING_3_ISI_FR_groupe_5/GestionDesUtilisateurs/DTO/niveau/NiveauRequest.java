package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NiveauRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le code est obligatoire")
    private String code;

    @NotNull(message = "L'ordre est obligatoire")
    @Min(value = 1, message = "L'ordre doit être supérieur à 0")
    private Integer ordre;

    @NotNull(message = "La filière est obligatoire")
    private Long filiereId;

    // ✅ Optionnel — ING5+ uniquement
    private Long specialiteId;
}