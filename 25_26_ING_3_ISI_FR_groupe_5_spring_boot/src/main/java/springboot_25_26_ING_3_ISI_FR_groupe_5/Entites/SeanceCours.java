package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Semestre> semestre=new ArrayList<>();
    @ManyToOne
    private Appels appels;
    @ManyToMany
    private Collection<Enseignant> enseignant= new ArrayList<>();
    @ManyToOne
    private Justificatif justificatif;
    @ManyToOne
    private UE ue;
}
