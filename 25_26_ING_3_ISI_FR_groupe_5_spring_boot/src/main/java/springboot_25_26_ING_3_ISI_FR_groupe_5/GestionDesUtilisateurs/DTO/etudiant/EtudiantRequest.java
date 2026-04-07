package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.etudiant;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EtudiantRequest {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    @NotNull(message = "La date de naissance est obligatoire")
    @Temporal(TemporalType.DATE)

    private LocalDate dateNaissance;

    @NotBlank(message = "Le téléphone est obligatoire")
    private String telephone;

    @NotNull(message = "La classe est obligatoire")
    private Long classeId;


    public void setDateNaissance(LocalDate dateNaissance) {
    }
}