package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Institut {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    private  String ville;
    private  String adresse;
    private  String email;
    private  String telephone;
    private  String localite;
    @OneToMany(mappedBy = "institut")
    private Collection<Ecole> ecoles;
    @ManyToMany
    private Collection<Utilisateur> utilisateurs= new ArrayList<>();
    @ManyToMany
    private  Collection<Administrateur> administrateur= new ArrayList<>();
}
