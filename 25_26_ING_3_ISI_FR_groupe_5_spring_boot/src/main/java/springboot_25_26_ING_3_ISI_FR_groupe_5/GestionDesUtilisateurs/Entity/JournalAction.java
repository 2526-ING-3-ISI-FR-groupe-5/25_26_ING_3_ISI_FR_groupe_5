package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "journal_action", indexes = {
        @Index(name = "idx_utilisateur_id", columnList = "utilisateur_id"),
        @Index(name = "idx_date_action", columnList = "date_action"),
        @Index(name = "idx_type_action", columnList = "type_action")
})
public class JournalAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== QUI A FAIT L'ACTION ? ==========
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    // ========== QUELLE ACTION ? ==========
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeAction typeAction;

    // ========== SUR QUOI ? ==========
    @Column(nullable = false)
    private String entiteConcernee;  // "Ecole", "Classe", "Etudiant", etc.

    private Long entiteId;           // ID de l'entité concernée

    @Column(length = 500)
    private String description;      // Description détaillée

    // ========== CONTEXTE TECHNIQUE ==========
    private String adresseIp;
    private String navigateur;       // User-Agent

    @Enumerated(EnumType.STRING)
    private StatutAction statut;

    // ========== QUAND ? ==========
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime dateAction;
}