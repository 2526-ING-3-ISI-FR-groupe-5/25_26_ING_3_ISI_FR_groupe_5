package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AssistantResponseDetails {
    private Long id;
    private String fonction;
    private String nom;
    private String prenom;
    private String email;
    private Date dateNaissance;
    protected  String telephone;
    protected boolean active ;
    protected boolean firstLogin;
    protected LocalDateTime createdAt;
    private Set<RoleResponseDTO> roles;
}
