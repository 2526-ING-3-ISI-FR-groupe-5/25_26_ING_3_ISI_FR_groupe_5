package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.PlageHoraire;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.ProgrammationUE;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;



@Entity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "niveau_id", nullable = false)
    private Niveau niveau;

    @OneToMany(mappedBy = "classe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PlageHoraire> plagesHoraires = new HashSet<>();

    @ManyToMany(mappedBy = "classes")
    @Builder.Default
    private Set<AssistantPedagogique> assistants = new HashSet<>();

    @OneToMany(mappedBy = "classe")
    @Builder.Default
    private Set<Inscription> inscriptions = new HashSet<>();

    @OneToMany(mappedBy = "classe")
    @Builder.Default
    private Set<ProgrammationUE> programmations = new HashSet<>();

    // ══════════════════════════════════════════
    // HELPERS POUR PLAGEHORAIRE
    // ══════════════════════════════════════════

    public void addPlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.add(plageHoraire);
        plageHoraire.setClasse(this);
    }

    public void removePlageHoraire(PlageHoraire plageHoraire) {
        plagesHoraires.remove(plageHoraire);
        plageHoraire.setClasse(null);
    }

    // ══════════════════════════════════════════
    // HELPERS POUR PROGRAMMATIONUE
    // ══════════════════════════════════════════

    public void addProgrammation(ProgrammationUE programmation) {
        programmations.add(programmation);
        programmation.setClasse(this);
    }

    public void removeProgrammation(ProgrammationUE programmation) {
        programmations.remove(programmation);
        programmation.setClasse(null);
    }

    // ══════════════════════════════════════════
    // HELPERS POUR ASSISTANTS
    // ══════════════════════════════════════════

    public void addAssistant(AssistantPedagogique assistant) {
        assistants.add(assistant);
        assistant.getClasses().add(this);
    }

    public void removeAssistant(AssistantPedagogique assistant) {
        assistants.remove(assistant);
        assistant.getClasses().remove(this);
    }

    // ══════════════════════════════════════════
    // HELPERS POUR INSCRIPTIONS
    // ══════════════════════════════════════════

    public void addInscription(Inscription inscription) {
        inscriptions.add(inscription);
        inscription.setClasse(this);
    }

    public void removeInscription(Inscription inscription) {
        inscriptions.remove(inscription);
        inscription.setClasse(null);
    }

    // ══════════════════════════════════════════
    // HELPERS DE VÉRIFICATION
    // ══════════════════════════════════════════

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

    public List<PlageHoraire> getPlagesForSemestre(Long semestreId) {
        return plagesHoraires.stream()
                .filter(p -> p.getSemestre() != null && p.getSemestre().getId().equals(semestreId))
                .collect(Collectors.toList());
    }

    public List<PlageHoraire> getPlagesForCurrentSemestre() {
        return plagesHoraires.stream()
                .filter(PlageHoraire::isDansSemestreActif)
                .collect(Collectors.toList());
    }
}