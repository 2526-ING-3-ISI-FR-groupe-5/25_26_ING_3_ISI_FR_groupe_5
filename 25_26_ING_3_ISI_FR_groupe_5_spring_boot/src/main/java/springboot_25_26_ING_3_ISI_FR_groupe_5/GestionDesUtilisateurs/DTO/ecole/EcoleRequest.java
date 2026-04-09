package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoleRequest {

    @NotBlank(message = "Le nom de l'école est obligatoire")
    private String nom;

    private String adresse;

    @Email(message = "Format d'email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{8,12}$", message = "Format de téléphone invalide")
    private String telephone;

    @NotNull(message = "Veuillez sélectionner un institut")
    private Long institutId;
}