package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.Num_semestre;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Semestre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Enumerated()

    private Num_semestre semestre;
    private Date date_debut;
    private Date date_fin;
    @ManyToOne
    private Annee_academique annee_academique;
    @ManyToMany(mappedBy = "semestres")
    private Collection<SeanceCours> seancecours;
}
//1FQCOMJH9J8@2025