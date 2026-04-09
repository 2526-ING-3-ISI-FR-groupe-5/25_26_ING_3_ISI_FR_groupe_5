package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.institut;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstitutRequest {

    @NotBlank(message = "Le nom de l'institut est obligatoire")
    private String nom;

    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    private String adresse;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @Pattern(regexp = "^(\\+\\d{1,3}[- ]?)?\\d{8,12}$", message = "Le numéro de téléphone doit être valide")
    private String telephone;

    private String localite;
}