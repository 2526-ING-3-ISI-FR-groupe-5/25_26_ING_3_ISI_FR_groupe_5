package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalActionResponse {

    private Long id;

    // ============================================
    // Qui a fait l'action ?
    // ============================================
    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private String utilisateurEmail;
    private String utilisateurType;     // ETD, ENS, SRV, AST

    // ============================================
    // Quelle action ?
    // ============================================
    private TypeAction typeAction;
    private String typeActionLibelle;   // version lisible humaine

    // ============================================
    // Sur quoi ?
    // ============================================
    private String entiteConcernee;
    private Long entiteId;
    private String description;

    // ============================================
    // Contexte technique
    // ============================================
    private String adresseIp;
    private String navigateur;
    private StatutAction statut;

    // ============================================
    // Quand ?
    // ============================================
    private LocalDateTime dateAction;
}