package springboot_25_26_ING_3_ISI_FR_groupe_5.DTO;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class UtilisateurDTO {

    private Long id;
    private String nom;
    private String email;
    private String telephone;
    private String adresse;
    private String role;
    private String status; // ACTIF, SUSPENDU, SUPPRIME
    private LocalDateTime dateCreation;
    private LocalDateTime derniereConnexion;


}

