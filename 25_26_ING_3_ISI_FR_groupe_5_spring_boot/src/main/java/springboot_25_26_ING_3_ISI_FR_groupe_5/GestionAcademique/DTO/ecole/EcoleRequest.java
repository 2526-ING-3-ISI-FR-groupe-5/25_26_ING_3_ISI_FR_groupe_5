package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ecole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Getter
@Setter
public class EcoleRequest {

    @NotBlank(message = "Le nom de l'école est obligatoire")
    private String nom;

    private String adresse;

    // ✅ Ajoutés
    private String ville;
    private String localite;

    @Email(message = "Format d'email invalide")
    private String email;

    // ✅ Regex plus flexible
    @Pattern(
            regexp = "^(\\+?\\d{1,3}[- ]?)?\\d{8,15}$",
            message = "Format de téléphone invalide"
    )
    private String telephone;

    @NotNull(message = "Veuillez sélectionner un institut")
    private Long institutId;

    // ✅ Statut actif/inactif
    private boolean active = true;
}