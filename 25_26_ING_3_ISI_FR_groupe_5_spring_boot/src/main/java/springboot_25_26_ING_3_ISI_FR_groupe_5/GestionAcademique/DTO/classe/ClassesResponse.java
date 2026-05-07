package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.classe;

import lombok.*;

import java.time.LocalDateTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Niveau;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassesResponse {

    // ============================================
    // Infos de base
    // ============================================

    private Long id;
    private String nom;

    // ✅ Statut
    private boolean active;
    private Integer capaciteMax;
    private boolean pleine;

    // ============================================
    // Niveau
    // ============================================

    private Long niveauId;
    private String niveauNom;
    private String niveauCode;
    private Integer niveauOrdre;

    // ============================================
    // Spécialité
    // ============================================

    private Long specialiteId;
    private String specialiteNom;
    private String specialiteCode;

    // ============================================
    // Filière
    // ============================================

    private Long filiereId;
    private String filiereNom;
    private String filiereCode;

    // ============================================
    // Cycle
    // ============================================

    // ✅ Ajouté
    private Long cycleId;
    private String cycleNom;

    // ============================================
    // École
    // ============================================

    // ✅ Ajouté
    private Long ecoleId;
    private String ecoleNom;

    // ============================================
    // Institut
    // ============================================

    private Long institutId;
    private String institutNom;
    private String institutVille;

    // ============================================
    // Statistiques uniquement — pas de listes
    // ============================================

    private int nombreEtudiants;
    private int nombrePlagesHoraires;
    private int nombreProgrammations;
    private int nombreTotalInscriptions;

    // ============================================
    // Audit
    // ============================================

    // ✅ Ajouté
    private LocalDateTime createdAt;
    private String creePar;
}