package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Export;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtudiantExportDTO {

    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String classeNom;
    private String niveauNom;
    private String filiereNom;
    private String anneeAcademique;
    private String semestre;
    private String statutInscription;
    private String decisionFinAnnee;
    private boolean active;
}