package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur;

import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response.RoleResponseDTO;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class SurveillantResponseDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private boolean active;
    private String secteur;
    private String typeContrat;
    private Set<RoleResponseDTO> roles;
}