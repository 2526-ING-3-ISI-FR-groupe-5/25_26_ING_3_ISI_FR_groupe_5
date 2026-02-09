package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
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
    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<SeanceCours> seancecours;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Appels> appels;
}
