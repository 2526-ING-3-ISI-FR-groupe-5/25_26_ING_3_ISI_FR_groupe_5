package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Resquest;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

import java.util.List;

@Data
public class UtilisateurUpdateDTO {

    private Long id;

    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caractères")
    private String nom;

    @Size(min = 2, max = 50, message = "Le prénom doit contenir entre 2 et 50 caractères")
    private String prenom;

    @Email(message = "Format d'email invalide")
    private String email;

    private String telephone;
    private String adresse;
    private TypeSexe sexe;
    private Boolean active;
    private Boolean enabled;
    private Boolean accountLocked;

    private List<Long> roleIds;
    private List<Long> institutIds;
}
