package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String code;
    private String niveau;
    private String description;

    @OneToMany(mappedBy = "filiere")
    private Collection<Etudiant> etudiants;
    @OneToMany(mappedBy = "filiere")
    private Collection<Cycle> cycles;
}
