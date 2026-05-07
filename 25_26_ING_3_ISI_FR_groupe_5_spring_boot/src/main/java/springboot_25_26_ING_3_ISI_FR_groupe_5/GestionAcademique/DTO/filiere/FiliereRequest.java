package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.filiere;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;

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

    // ✅ Optionnel — cohérent avec l'entité
    private Long cycleId;

    // ✅ Statut actif/inactif
    private boolean active = true;
}