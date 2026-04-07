package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Administrateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Institut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String ville;
    private String adresse;
    private String email;
    private String telephone;
    private String localite;

    // ✅ Un institut peut avoir plusieurs écoles
    @OneToMany(mappedBy = "institut", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Collection<Ecole> ecoles = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private Collection<Utilisateur> utilisateurs = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    private Collection<Administrateur> administrateur = new ArrayList<>();

    // Helper
    public void addEcole(Ecole ecole) {
        ecoles.add(ecole);
        ecole.setInstitut(this);
    }

    public void removeEcole(Ecole ecole) {
        ecoles.remove(ecole);
        ecole.setInstitut(null);
    }
}