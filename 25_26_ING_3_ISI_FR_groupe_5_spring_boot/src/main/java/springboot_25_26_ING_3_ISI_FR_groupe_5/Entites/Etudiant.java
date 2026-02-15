package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeNiveau;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeSexe;

import java.util.Collection;
import java.util.Date;

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
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateNaissance;
     @ManyToOne
    private Parent parent;
     @ManyToOne
    private Filiere filiere;
    @ManyToMany
    private Collection<Appels> appels;

}
