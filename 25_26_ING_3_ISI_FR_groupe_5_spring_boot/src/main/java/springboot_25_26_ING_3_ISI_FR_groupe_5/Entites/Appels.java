package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Appels {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
@Temporal(TemporalType.DATE)
@DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date_debut;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date_fin;
    private Long Nbre_heures;
    @ManyToMany
    private Collection<Surveillant> surveillant=new ArrayList<>();
    @ManyToMany
    private Collection<Enseignant> enseignant= new ArrayList<>();
    @OneToMany(mappedBy = "appels")
    private Collection<Etudiant> etudiant;
    @ManyToOne
    private AssistantPedagogique assistantPedagogique;
    @OneToMany(mappedBy = "appels")
    private Collection<SeanceCours> seanceCours;
    @ManyToOne
    private ValidationPresence validationPresence;
}
