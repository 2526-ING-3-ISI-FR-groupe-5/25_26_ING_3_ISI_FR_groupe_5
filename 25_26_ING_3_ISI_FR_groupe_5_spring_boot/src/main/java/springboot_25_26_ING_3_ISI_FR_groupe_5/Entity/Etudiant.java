package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Inscription;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ETD")
public class Etudiant extends Utilisateur {

    private String matricule;

    @Enumerated(EnumType.STRING)
    private TypeNiveau niveau;

    // ==================== Relations ====================

    @ManyToOne(fetch = FetchType.LAZY)
    private Filiere filiere;

    @ManyToOne(fetch = FetchType.LAZY)
    private Classe classe;

    // ✅ Relation vers Parent (un étudiant peut avoir un parent)
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @OneToMany(mappedBy = "etudiant")
    private Set<Inscription> inscriptions = new HashSet<>();

    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Appels> appels = new HashSet<>();

    // ==================== Méthodes ====================

    private String generateMatricule() {
        String year = String.valueOf(java.time.Year.now().getValue());
        long sequence = System.nanoTime() % 100000;
        return String.format("ETU-%s-%05d", year, sequence);
    }

    public void addAppel(Appels appel) {
        appels.add(appel);
        appel.setEtudiant(this);
    }

    public void removeAppel(Appels appel) {
        appels.remove(appel);
        appel.setEtudiant(null);
    }
}