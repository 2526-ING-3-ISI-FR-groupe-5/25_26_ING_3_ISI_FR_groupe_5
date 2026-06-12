package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UtilisateurRequest {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private String typeUtilisateur;
    private Long roleId;
    private List<Long> permissionsDesactivees;
    private List<Long> classesIds;
    private String grade;
    private String typeEnseignant;
    private List<Long> programmationIds;

    private String fonction;
    private String secteur;
    private String typeContrat;
}