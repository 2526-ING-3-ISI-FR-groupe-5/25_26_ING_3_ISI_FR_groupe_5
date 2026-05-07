package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;



import lombok.*;

import java.util.Date;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Enseignant;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeEnseignant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurListDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Date dateNaissance;
    private boolean active;
    private String type;          // "ENS" ou "ASS"
    private String grade;         // Enseignant seulement
    private String typeEnseignant;// Enseignant seulement
    private String fonction;      // Assistant seulement
    private Set<RoleResponseDTO> roles;


}
