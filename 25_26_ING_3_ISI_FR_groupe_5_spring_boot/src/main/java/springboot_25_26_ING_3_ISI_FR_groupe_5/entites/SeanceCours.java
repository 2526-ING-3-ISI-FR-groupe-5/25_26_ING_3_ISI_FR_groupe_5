package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class SeanceCours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    private  String titre;
    private Long  nb_heure;
    private  Long nb_credit;
    @ManyToOne
    private Collection<Justificatif>  justificatifs;
    @ManyToOne
    private Collection<UE> ues;
    @OneToMany(mappedBy = "seancecours")
    private Collection<Appels> appels;
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Semestre> semestres;
    @ManyToMany(mappedBy = "seancecours")
    private Collection<Enseignant> enseignants;
}
