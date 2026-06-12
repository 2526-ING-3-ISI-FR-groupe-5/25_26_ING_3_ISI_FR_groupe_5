package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.role.RoleResponse;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnseignantResponseDetails {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private String telephone;
    private boolean active;
    private boolean firstLogin;
    private LocalDateTime createdAt;

    // Champs spécifiques Enseignant
    private String grade;
    private String typeEnseignant;

    // Multi-instituts
    private Long institutId;
    private String institutNom;

    private Set<RoleResponse> roles;
}