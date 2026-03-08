package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;



import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enums.TypeSexe;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurListDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private TypeSexe sexe;
    private String type;
    private String status;
    private Boolean active;
    private String nomComplet; // nom + prénom

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionResponseDTO {
        private Long id;
        private String nom;
        private Boolean active;
        private Date dateCreation;
    }
}
