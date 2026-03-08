package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Resquest;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionRequestDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String description;
    private Boolean active;
}
