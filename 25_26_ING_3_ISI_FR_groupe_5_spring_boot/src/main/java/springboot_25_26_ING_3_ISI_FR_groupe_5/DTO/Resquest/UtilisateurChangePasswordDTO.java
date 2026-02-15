package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UtilisateurChangePasswordDTO {

    @NotBlank(message = "L'email est requis")
    private String email;

    @NotBlank(message = "L'ancien mot de passe est requis")
    private String oldPassword;

    @NotBlank(message = "Le nouveau mot de passe est requis")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Le nouveau mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String newPassword;

    @NotBlank(message = "La confirmation est requise")
    private String confirmPassword;
}