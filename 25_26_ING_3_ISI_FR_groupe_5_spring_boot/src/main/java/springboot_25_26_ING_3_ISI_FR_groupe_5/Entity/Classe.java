package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Classe extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    // ========== RELATIONS ==========

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

    // ========== MÉTHODES UTILITAIRES ==========

    public Specialite getSpecialite() {
        return niveau != null ? niveau.getSpecialite() : null;
    }

    public String getSpecialiteNom() {
        Specialite spec = getSpecialite();
        return spec != null ? spec.getNom() : null;
    }

    public String getSpecialiteCode() {
        Specialite spec = getSpecialite();
        return spec != null ? spec.getCode() : null;
    }

    public Filiere getFiliere() {
        return niveau != null && niveau.getSpecialite() != null
                ? niveau.getSpecialite().getFiliere() : null;
    }

    public String getFiliereNom() {
        Filiere filiere = getFiliere();
        return filiere != null ? filiere.getNom() : null;
    }

    public Cycle getCycle() {
        Filiere filiere = getFiliere();
        return filiere != null ? filiere.getCycle() : null;
    }

    public String getCycleNom() {
        Cycle cycle = getCycle();
        return cycle != null && cycle.getTypeCycle() != null
                ? cycle.getTypeCycle().getLibelle() : null;
    }

    // Helpers pour PlageHoraire
    public void addPlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.add(plageHoraire);
        plageHoraire.setClasse(this);
    }

    public void removePlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.remove(plageHoraire);
        plageHoraire.setClasse(null);
    }

    // Helpers pour Inscription
    public void addInscription(Inscription inscription) {
        inscriptions.add(inscription);
        inscription.setClasse(this);
    }

    public void removeInscription(Inscription inscription) {
        inscriptions.remove(inscription);
        inscription.setClasse(null);
    }

    // Helpers pour ProgrammationUE
    public void addProgrammation(ProgrammationUE programmation) {
        programmations.add(programmation);
        programmation.setClasse(this);
    }

    public void removeProgrammation(ProgrammationUE programmation) {
        programmations.remove(programmation);
        programmation.setClasse(null);
    }
}