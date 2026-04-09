package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Justificatif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private String fichierUrl;

    private LocalDateTime dateDebutAbsence;
    private LocalDateTime dateFinAbsence;
    private Long nombreHeures;

    @Enumerated(EnumType.STRING)
    private StatutJustificatif status;

    private String commentaireValidation;

    private LocalDateTime dateSoumission;
    private LocalDateTime dateValidation;

    @Enumerated(EnumType.STRING)
    private TypeJustificatif type;


    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "assistant_pedagogique_id")
    private AssistantPedagogique assistantPedagogique;

    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private Utilisateur validateur;

    @OneToMany(mappedBy = "justificatifs")
    @Builder.Default
    private Collection<SeanceCours> seance = new ArrayList<>();

    @ManyToMany(mappedBy = "justificatifs")
    @Builder.Default
    private Collection<Fichier> fichiers = new ArrayList<>();
}