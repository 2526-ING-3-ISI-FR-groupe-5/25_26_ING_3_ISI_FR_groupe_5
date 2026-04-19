package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Auditable;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ecole extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;
    private String email;
    private String telephone;
    @ManyToOne
    @JoinColumn(name = "institut_id", nullable = false)
    private Institut institut;

    @OneToMany(mappedBy = "ecole", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Filiere> filieres = new HashSet<>();
}