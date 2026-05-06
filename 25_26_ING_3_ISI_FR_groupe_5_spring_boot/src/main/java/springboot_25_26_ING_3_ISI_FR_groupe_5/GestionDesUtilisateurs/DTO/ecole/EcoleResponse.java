package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.ecole;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EcoleResponse {

    private Long id;
    private String nom;
    private String adresse;

    // ✅ Ajoutés
    private String ville;
    private String localite;

    private String email;
    private String telephone;

    // ✅ Statut
    private boolean active;

    // ✅ Institut
    private Long institutId;
    private String institutNom;

    // ✅ Stats
    private int nombreFilieres;

    // ✅ Audit
    private LocalDateTime createdAt;
    private String creePar;
}