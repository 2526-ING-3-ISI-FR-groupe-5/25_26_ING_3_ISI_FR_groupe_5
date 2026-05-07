package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesPresences.Entity.ProgrammationUE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutInscription;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ✅ Contrainte unicité nom par niveau
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "uk_classe_nom_niveau",
                columnNames = {"nom", "niveau_id"}
        )
)
// ✅ Nécessaire pour Auditable
@EntityListeners(AuditingEntityListener.class)
public class Classe extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ============================================
    // Attributs propres
    // ============================================

    @Column(nullable = false)
    private String nom;

    // ✅ Capacité max de la classe
    private Integer capaciteMax;

    // ✅ Statut actif/inactif
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    // ============================================
    // Relations
    // ============================================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlageHoraire> plagesHoraires = new HashSet<>();

    @OneToMany(mappedBy = "classe")
    @Builder.Default
    private Set<Inscription> inscriptions = new HashSet<>();

    @OneToMany(mappedBy = "classe")
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();

    // ============================================
    // Helpers — navigation académique
    // ============================================

    public Specialite getSpecialite() {
        return niveau != null ? niveau.getSpecialite() : null;
    }

    public String getSpecialiteNom() {
        Specialite spec = getSpecialite();
        return spec != null ? spec.getNom() : null;
    }

    public Filiere getFiliere() {
        // ✅ Via spécialité — chemin correct
        Specialite spec = getSpecialite();
        return spec != null ? spec.getFiliere() : null;
    }

    public Cycle getCycle() {
        Filiere filiere = getFiliere();
        return filiere != null ? filiere.getCycle() : null;
    }

    // ============================================
    // Helpers — multi-instituts
    // ✅ Chemin correct : niveau → specialite → filiere → ecole → institut
    // ============================================

    public Institut getInstitut() {
        Filiere filiere = getFiliere();
        if (filiere != null && filiere.getEcole() != null) {
            return filiere.getEcole().getInstitut();
        }
        return null;
    }

    public Long getInstitutId() {
        Institut institut = getInstitut();
        return institut != null ? institut.getId() : null;
    }

    public String getInstitutNom() {
        Institut institut = getInstitut();
        return institut != null ? institut.getNom() : null;
    }

    // ============================================
    // Helpers — étudiants
    // ============================================

    // ✅ Compte uniquement les inscriptions actives
    public int getNombreEtudiants() {
        if (inscriptions == null) return 0;
        return (int) inscriptions.stream()
                .filter(i -> i.getStatut() == StatutInscription.ACTIF
                        || i.getStatut() == StatutInscription.VALIDE)
                .count();
    }

    // ✅ Total toutes inscriptions
    public int getNombreTotalInscriptions() {
        return inscriptions != null ? inscriptions.size() : 0;
    }

    // ✅ Vérifie si la classe est pleine
    public boolean isPleine() {
        return capaciteMax != null
                && getNombreEtudiants() >= capaciteMax;
    }

    // ============================================
    // Helpers — ajout/suppression
    // ============================================

    public void addPlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.add(plageHoraire);
        plageHoraire.setClasse(this);
    }

    public void removePlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.remove(plageHoraire);
        plageHoraire.setClasse(null);
    }

    public void addInscription(Inscription inscription) {
        inscriptions.add(inscription);
        inscription.setClasse(this);
    }

    public void removeInscription(Inscription inscription) {
        inscriptions.remove(inscription);
        inscription.setClasse(null);
    }

    public void addProgrammation(ProgrammationUE programmation) {
        programmations.add(programmation);
        programmation.setClasse(this);
    }

    public void removeProgrammation(ProgrammationUE programmation) {
        programmations.remove(programmation);
        programmation.setClasse(null);
    }
}