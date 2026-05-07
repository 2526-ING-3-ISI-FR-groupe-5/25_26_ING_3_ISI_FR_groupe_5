package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classes;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassesResponse {

    private Long id;
    private String nom;

    // ========== Infos Niveau ==========
    private Long niveauId;
    private String niveauNom;
    private Integer niveauOrdre;

    // ========== Spécialité (via niveau) ==========
    private Long specialiteId;
    private String specialiteNom;
    private String specialiteCode;

    // ========== Filière (via specialite) ==========
    private Long filiereId;
    private String filiereNom;

    // ========== Cycle (via filiere) ==========
    private Long cycleId;
    private String cycleNom;

    // ========== École (via filiere) ==========
    private Long ecoleId;
    private String ecoleNom;

    // ========== Institut (via ecole) ==========
    private Long institutId;
    private String institutNom;

    // ========== Statistiques ==========
    private Integer nombreEtudiants;
    private Integer nombreProgrammations;
}