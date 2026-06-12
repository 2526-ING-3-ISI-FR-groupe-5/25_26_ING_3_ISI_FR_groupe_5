package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurExportDTO {

    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String type;
    private String grade;
    private String fonction;
    private String secteur;
    private boolean active;
    private String institutNom;
    private LocalDate dateNaissance;
}