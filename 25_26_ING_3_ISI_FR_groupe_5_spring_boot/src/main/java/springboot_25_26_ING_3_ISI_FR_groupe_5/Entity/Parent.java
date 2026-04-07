package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("PRT")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Parent extends Utilisateur {

    private String profession;
    private String telephone2;

    // ✅ Un parent peut avoir plusieurs enfants (étudiants)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Etudiant> etudiants = new HashSet<>();

    // Helpers
    public void addEtudiant(Etudiant etudiant) {
        etudiants.add(etudiant);
        etudiant.setParent(this);
    }

    public void removeEtudiant(Etudiant etudiant) {
        etudiants.remove(etudiant);
        etudiant.setParent(null);
    }
}