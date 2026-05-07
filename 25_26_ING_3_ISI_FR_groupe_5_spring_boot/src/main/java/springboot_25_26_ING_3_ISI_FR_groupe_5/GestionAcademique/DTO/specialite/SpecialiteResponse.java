package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.specialite;

import lombok.*;

import java.time.LocalDateTime;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpecialiteResponse {

    private Long id;
    private String nom;
    private String code;
    private String description;

    // ✅ Statut
    private boolean active;

    // ✅ Filière
    private Long filiereId;
    private String filiereNom;
    private String filiereCode;

    // ✅ École — navigation multi-instituts
    private Long ecoleId;
    private String ecoleNom;

    // ✅ Institut — navigation multi-instituts
    private Long institutId;
    private String institutNom;

    // ✅ Stats
    private int nombreNiveaux;
    private int nombreClasses;

    // ✅ Audit
    private LocalDateTime createdAt;
    private String creePar;
}