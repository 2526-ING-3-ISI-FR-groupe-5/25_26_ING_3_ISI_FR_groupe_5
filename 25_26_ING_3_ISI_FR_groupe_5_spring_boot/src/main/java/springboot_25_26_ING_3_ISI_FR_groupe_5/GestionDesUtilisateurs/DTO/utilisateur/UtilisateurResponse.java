package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.RoleResponseDTO;

import java.util.Date;
import java.util.Set;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurResponse {


    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private Date dateNaissance;
    private boolean active;
    private String type;
    private String grade;
    private String secteur;
    private String typeContrat;
    // Enseignant seulement
    private String typeEnseignant;
    private String fonction;
    private Set<RoleResponseDTO> roles;
}
