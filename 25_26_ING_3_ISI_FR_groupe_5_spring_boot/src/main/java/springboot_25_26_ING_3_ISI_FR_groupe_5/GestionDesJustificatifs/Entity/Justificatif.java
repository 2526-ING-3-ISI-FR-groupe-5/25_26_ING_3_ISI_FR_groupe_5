package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Enum.TypeJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Etudiant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Justificatif extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String contenu;

    private String fichierUrl;

    @Column(nullable = false)
    private LocalDateTime dateDebutAbsence;

    @Column(nullable = false)
    private LocalDateTime dateFinAbsence;

    private Long nombreHeures;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutJustificatif statut = StatutJustificatif.EN_ATTENTE;

    @Column(length = 1000)
    private String commentaireValidation;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dateSoumission = LocalDateTime.now();

    private LocalDateTime dateValidation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeJustificatif type;

    // ══════════════════════════════════════════
    // RELATIONS
    // ══════════════════════════════════════════

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assistant_pedagogique_id")
    private AssistantPedagogique assistantPedagogique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validateur_id")
    private Utilisateur validateur;

    @OneToMany(mappedBy = "justificatif", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════

    public void addAppel(Appels appel) {
        appels.add(appel);
        appel.setJustificatif(this);
    }

    public void removeAppel(Appels appel) {
        appels.remove(appel);
        appel.setJustificatif(null);
    }

    public boolean isEnAttente() {
        return statut == StatutJustificatif.EN_ATTENTE;
    }

    public boolean isValide() {
        return statut == StatutJustificatif.VALIDE;
    }

    public boolean isRefuse() {
        return statut == StatutJustificatif.REFUSE;
    }

    // Multi-instituts
    public Long getInstitutId() {
        if (etudiant != null && etudiant.getInstitut() != null) {
            return etudiant.getInstitut().getId();
        }
        return null;
    }

    public String getInstitutNom() {
        if (etudiant != null && etudiant.getInstitut() != null) {
            return etudiant.getInstitut().getNom();
        }
        return null;
    }
}