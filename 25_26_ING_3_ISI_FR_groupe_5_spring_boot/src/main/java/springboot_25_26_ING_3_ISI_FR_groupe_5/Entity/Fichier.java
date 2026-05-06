package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Fichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomFichier;
    private String cheminFichier;      // 🆕 Chemin du fichier sur le disque
    private String typeFichier;        // 🆕 Type MIME (application/pdf, image/jpeg...)
    private Long taille;               // 🆕 Taille en octets

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    // ✅ Propriétaire de la relation → @JoinTable ici
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "fichier_justificatif",
            joinColumns = @JoinColumn(name = "fichier_id"),
            inverseJoinColumns = @JoinColumn(name = "justificatif_id")
    )
    @Builder.Default
    private Set<Justificatif> justificatifs = new HashSet<>();  // ✅ Set plutôt que Collection

    // ══════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════

    public void addJustificatif(Justificatif justificatif) {
        justificatifs.add(justificatif);
        justificatif.getFichiers().add(this);
    }

    public void removeJustificatif(Justificatif justificatif) {
        justificatifs.remove(justificatif);
        justificatif.getFichiers().remove(this);
    }
}