package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.specialite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;

@Getter
@Setter
public class SpecialiteRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    // ✅ Validation longueur ajoutée
    @NotBlank(message = "Le code est obligatoire")
    @Size(min = 2, max = 10,
            message = "Le code doit faire entre 2 et 10 caractères")
    private String code;

    private String description;

    @NotNull(message = "La filière est obligatoire")
    private Long filiereId;

    // ✅ Statut actif/inactif
    private boolean active = true;
}