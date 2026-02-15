package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.Response;
import springboot_25_26_ING_3_ISI_FR_groupe_5.DTO.RoleDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UtilisateurResponseDTO {

    // Informations de base
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private TypeSexe sexe;

    // Statut du compte
    private Boolean active;
    private boolean firstLogin;
    private boolean enabled;
    private boolean accountLocked;
    private String status;
    private int loginAttempts;

    // Dates
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLogin;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;

    // Type d'utilisateur
    private String type;
    private String typeLibelle; // "Administrateur", "Enseignant", etc.

    // Relations (IDs uniquement pour éviter les références circulaires)
    private List<RoleDTO> roles = new ArrayList<>();
    private List<Long> institutIds = new ArrayList<>();
    private List<String> permissions = new ArrayList<>();
}
