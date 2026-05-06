package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.filiere;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FiliereResponse {

    private Long id;
    private String nom;
    private String code;
    private String description;

    // ✅ Statut
    private boolean active;

    // ✅ École
    private Long ecoleId;
    private String ecoleNom;
    private String ecoleVille;

    // ✅ Institut — navigation multi-instituts
    private Long institutId;
    private String institutNom;

    // ✅ Cycle
    private Long cycleId;
    private String cycleNom;
    private String cycleLibelle;

    // ✅ Stats
    private int nombreSpecialites;
    private int nombreNiveaux;

    // ✅ Audit
    private LocalDateTime createdAt;
    private String creePar;
}