package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.niveau;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NiveauResponse {

    private Long id;
    private String nom;
    private String code;
    private Integer ordre;

    // ✅ Statut
    private boolean active;

    // ✅ Filière
    private Long filiereId;
    private String filiereNom;
    private String filiereCode;

    // ✅ Spécialité (optionnel)
    private Long specialiteId;
    private String specialiteNom;
    private String specialiteCode;

    // ✅ École — navigation multi-instituts
    private Long ecoleId;
    private String ecoleNom;

    // ✅ Institut — navigation multi-instituts
    private Long institutId;
    private String institutNom;

    // ✅ Migration
    private boolean hasNiveauSuperieur;

    // ✅ Stats
    private int nombreClasses;
    private int nombreEtudiants;

    // ✅ Audit
    private LocalDateTime createdAt;
    private String creePar;
}