package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EnseignantResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private Date dateNaissance;
    private String grade;
    private String typeEnseignant;
    private Set<RoleResponseDTO> roles;
}
