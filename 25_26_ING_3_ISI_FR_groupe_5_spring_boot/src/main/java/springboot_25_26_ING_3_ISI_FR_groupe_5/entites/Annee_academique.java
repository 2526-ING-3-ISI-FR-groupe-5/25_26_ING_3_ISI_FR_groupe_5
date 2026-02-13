package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity

public class Annee_academique {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    private Date date_debut;
    private Date date_fin;
    @OneToMany(mappedBy = "annee_academique")
    private Collection<Semestre> semestres;

}
