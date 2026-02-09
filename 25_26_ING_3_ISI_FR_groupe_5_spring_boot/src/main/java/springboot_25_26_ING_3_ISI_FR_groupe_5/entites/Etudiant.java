package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;
import jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeSexe;

import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ETD")
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
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date_naissance;
     @ManyToOne
    private Parent parent;
     @ManyToOne
    private Filiere filiere;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Appels> appels;

}
