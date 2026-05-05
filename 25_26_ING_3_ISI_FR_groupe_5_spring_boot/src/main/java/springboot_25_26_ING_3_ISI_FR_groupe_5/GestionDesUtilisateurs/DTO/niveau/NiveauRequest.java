package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NiveauRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    // ✅ Validation longueur ajoutée
    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 2, max = 10,
            message = "Le code doit faire entre 2 et 10 caractères")
    private String code;

    @NotNull(message = "L'ordre est obligatoire")
    @Min(value = 1, message = "L'ordre doit être supérieur à 0")
    private Integer ordre;

    @NotNull(message = "La filière est obligatoire")
    private Long filiereId;

    // ✅ Optionnel — spécialités uniquement
    private Long specialiteId;

    // ✅ Statut actif/inactif
    private boolean active = true;
}