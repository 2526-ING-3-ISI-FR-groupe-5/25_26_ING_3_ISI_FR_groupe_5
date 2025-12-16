package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;
import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Etudiant extends Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;
    private String email;
    private String adresse;
    private String telephone;
    private String matricule;

    @Enumerated(EnumType.STRING)
    private TypeNiveau niveau;

    @Enumerated(EnumType.STRING)
    private TypeSexe sexe;

    private Date date_naissance;
     @ManyToOne
    private Parent parent;
     @ManyToOne
    private Filiere filiere;
     @ManyToOne
    private Appels appels;
}
