package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Resquest;

import jakarta.validation.constraints.NotBlank;

public class ParentChangePassword {
    private  Long id;
    @NotBlank(message = "La confirmation est requise")
    private String password;
}
