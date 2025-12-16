package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Data
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
    @OneToMany(mappedBy ="appels")
    private Collection<Etudiant> etudiants;
}
