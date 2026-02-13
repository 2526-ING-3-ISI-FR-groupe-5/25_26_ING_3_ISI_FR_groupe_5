package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
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
    private Date date;
    private Date date_debut;
    private Date date_fin;
    private Long Nbre_heures;
    @ManyToOne
    private SeanceCours seanceCours;
    @ManyToMany(mappedBy = "appels")
    private Collection<Etudiant> etudiants;
    private Collection<Enseignant> enseignants;
    private Collection<Surveillant> surveillants;
}
