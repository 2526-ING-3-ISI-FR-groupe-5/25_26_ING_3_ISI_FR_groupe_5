package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeNiveau;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cycle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @Enumerated(EnumType.STRING)
    private TypeNiveau niveau;
    private String  specialite;
    @ManyToOne
    private Ecole ecoles;
    @ManyToOne
    private Filiere filiere;
}
