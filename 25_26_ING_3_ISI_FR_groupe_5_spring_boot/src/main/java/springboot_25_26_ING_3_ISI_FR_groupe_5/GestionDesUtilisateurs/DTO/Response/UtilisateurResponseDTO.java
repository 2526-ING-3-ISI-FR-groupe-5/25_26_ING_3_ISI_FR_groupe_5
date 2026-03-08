package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UtilisateurResponseDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Date dateNaissance;
    private boolean active;
    private String type;
    private String grade;
    private String typeEnseignant;
    private String fonction;
    private Set<RoleResponseDTO> roles;

}
