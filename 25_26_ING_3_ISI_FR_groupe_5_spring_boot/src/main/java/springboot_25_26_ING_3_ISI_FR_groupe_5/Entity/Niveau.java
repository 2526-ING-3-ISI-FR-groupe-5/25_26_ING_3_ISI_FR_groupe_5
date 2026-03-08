package springboot_25_26_ING_3_ISI_FR_groupe_5.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Niveau {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    private  String nom;
    @ManyToOne
    private  Cycle cycle;
    @OneToMany
    private Collection<Filiere> filiereCollection;
}
