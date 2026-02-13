package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ADM")
public class Administrateur extends Utilisateur{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nom;
    private String email;
    private  String telephone;
    @ManyToMany(mappedBy = "administrateurs")
    private Collection<Ecole> ecoles= new ArrayList<>();
    @ManyToMany(mappedBy = "administrateur")
    private  Collection<Institut> instituts= new ArrayList<>();
    @ManyToMany(mappedBy = "administrateurs")
    private  Collection<Filiere> filieres= new ArrayList<>();
    @ManyToMany(mappedBy = "admin")
    private  Collection<Utilisateur> utilisateurs= new ArrayList<>();

}
