package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesJustificatifs.Entity.Justificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.Appels;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ETD")
public class Etudiant extends Utilisateur {

    private String matricule;

    // ✅ SUPPRIMÉ — TypeNiveau niveau était redondant avec
    // etudiant.getClasse().getNiveau() et jamais mis à jour lors des migrations.
    // Lire le niveau via : etudiant.getClasse().getNiveau()

    // ==================== Relations ====================

    /**
     * ✅ Mis à jour automatiquement lors de chaque migration
     * par MigrationService.mettreAJourClasseEtudiant().
     * Toujours cohérent avec l'année académique active.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Filiere filiere;

    /**
     * ✅ Mis à jour automatiquement lors de chaque migration
     * par MigrationService.mettreAJourClasseEtudiant().
     * Pour obtenir la classe d'une année précise (historique),
     * utiliser : inscriptionRepo.findByEtudiantIdAndAnneeAcademiqueId()
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Classe classe;

    @OneToMany(mappedBy = "etudiant")
    @Builder.Default
    private Set<Inscription> inscriptions = new HashSet<>();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL)
    @Builder.Default
    private Collection<Justificatif> justificatifs = new ArrayList<>();

    // ==================== Helpers ====================

    /**
     * Raccourci pour obtenir le niveau actuel de l'étudiant.
     * Source de vérité : la classe courante, pas un champ dénormalisé.
     */
    public String getNiveauNom() {
        return classe != null && classe.getNiveau() != null
                ? classe.getNiveau().getNom()
                : null;
    }

    /**
     * Raccourci pour obtenir la filière via la classe (chemin canonique).
     * Priorité sur etudiant.filiere qui est le raccourci de navigation.
     */
    public String getFiliereNom() {
        if (classe != null && classe.getFiliere() != null) {
            return classe.getFiliere().getNom();
        }
        return filiere != null ? filiere.getNom() : null;
    }

    // ==================== Méthodes ====================

    public void addAppel(Appels appel) {
        appels.add(appel);
        appel.setEtudiant(this);
    }

    public void removeAppel(Appels appel) {
        appels.remove(appel);
        appel.setEtudiant(null);
    }
}