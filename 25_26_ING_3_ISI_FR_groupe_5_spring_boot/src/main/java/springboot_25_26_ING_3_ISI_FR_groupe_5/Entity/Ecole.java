package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.Administrateur;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Entity.AssistantPedagogique;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ecole {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    private  String adresse;
    private  String email;
    private  String telephone;
    @OneToMany(mappedBy = "ecoles")
    private Collection<Cycle> cycles;
    @OneToMany(mappedBy = "ecoles")
    private Collection<AssistantPedagogique> assistantPedagogiques;
    @ManyToOne
    private Institut institut;
    @OneToMany(mappedBy = "ecoles")
    private  Collection<Cycle> cycle;
    @ManyToMany
    private Collection<Administrateur> administrateurs= new ArrayList<>();
}
