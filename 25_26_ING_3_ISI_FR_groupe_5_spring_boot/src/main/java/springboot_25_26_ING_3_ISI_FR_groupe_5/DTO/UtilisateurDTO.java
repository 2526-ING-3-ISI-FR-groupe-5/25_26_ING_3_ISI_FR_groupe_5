package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UtilisateurDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String telephone;
    private TypeSexe sexe;
    private Boolean active;
    private boolean firstLogin;
    private boolean enabled;
    private boolean accountLocked;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateCreation;
    private LocalDateTime lastLogin;
    private int loginAttempts;

    // Type discriminateur pour savoir quel type d'utilisateur (ADM, ENS, ETD, etc.)
    private String type;
    private  String status;
    private List<Long> roleIds = new ArrayList<>();
    private List<Long> permissionIds = new ArrayList<>();
    private Long localisationId;
    private  List<Long> inscriptionIds = new ArrayList<>();
}

