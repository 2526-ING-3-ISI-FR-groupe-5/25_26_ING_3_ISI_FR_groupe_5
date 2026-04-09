package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Utilisateur;

import java.util.ArrayList;
import java.util.Collection;

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

    @ManyToOne
    private Utilisateur utilisateur;
    @ManyToMany
    @JoinTable(
            name = "fichier_justificatif",
            joinColumns = @JoinColumn(name = "fichier_id"),
            inverseJoinColumns = @JoinColumn(name = "justificatif_id")
    )
    @Builder.Default  
    private Collection<Justificatif> justificatifs = new ArrayList<>();
}