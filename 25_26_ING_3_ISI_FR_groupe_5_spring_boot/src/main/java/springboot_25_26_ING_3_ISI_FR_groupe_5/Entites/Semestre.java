package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.Num_semestre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
    @Enumerated(EnumType.STRING)

    private Num_semestre semestre;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date_debut;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date_fin;
    @ManyToOne
    private Annee_academique annee_academique;
    @ManyToMany(mappedBy = "semestre")
    private Collection<SeanceCours> seancecours= new ArrayList<>();
}
//1FQCOMJH9J8@2025