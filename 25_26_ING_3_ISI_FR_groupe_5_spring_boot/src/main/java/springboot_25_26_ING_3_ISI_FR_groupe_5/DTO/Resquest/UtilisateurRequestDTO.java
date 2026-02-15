package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;

/**
 * Creation D'un utilisateur
 */

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeUser;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UtilisateurRequestDTO {

    @NotBlank(message = "Le nom est requis")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est requis")
    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @NotBlank(message = "L'email est requis")
    @Email(message = "Format d'email invalide")
    private String email;

    @NotBlank(message = "Le mot de passe est requis")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String password;

    @NotNull(message = "Le sexe est requis")
    private TypeSexe sexe;

    private String telephone;
    private String adresse;

    @NotNull(message = "Le type d'utilisateur est requis")
    @Enumerated(EnumType.STRING)
    private TypeUser type; // ADM, ENS, ETD, ASP, PRT, SRV

    // Relations optionnelles à la création
    private Long localisationId;
    private List<Long> roleIds;
}