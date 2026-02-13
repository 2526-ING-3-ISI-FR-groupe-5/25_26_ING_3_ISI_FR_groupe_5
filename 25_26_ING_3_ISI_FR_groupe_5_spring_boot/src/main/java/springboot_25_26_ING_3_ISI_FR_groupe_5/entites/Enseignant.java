package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("ENS")

public class  Enseignant extends Utilisateur  {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private String nom;
    private String email;
    private String grade;
    private String specialite;
    @OneToMany(mappedBy = "enseignant")
    private Collection<ValidationPresence> validationPresences;
    @ManyToMany(mappedBy = "enseignant")
    private Collection<SeanceCours> seancecours =new ArrayList<>();
    @ManyToMany(mappedBy = "enseignant")
    private Collection<Appels> appels = new ArrayList<>();
}
