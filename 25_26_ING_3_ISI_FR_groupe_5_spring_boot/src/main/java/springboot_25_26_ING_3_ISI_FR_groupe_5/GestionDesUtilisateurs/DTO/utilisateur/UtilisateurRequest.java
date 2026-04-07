package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.utilisateur;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class UtilisateurRequest {

    // Étape 1 — Infos personnelles
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private LocalDate dateNaissance;
    private String typeUtilisateur; // "ENS", "AST", "SUR"

    // Étape 2 — Rôle + Permissions
    private Long roleId;
    private List<Long> permissionsDesactivees; // IDs des permissions à désactiver

    // Étape 3 — Classes (ENS et AST uniquement)
    private List<Long> classesIds;

    // Spécifique Enseignant
    private String grade;
    private String typeEnseignant;

    // Spécifique Assistant
    private String fonction;

    // Spécifique Surveillant
    private String secteur;
    private String typeContrat;
}