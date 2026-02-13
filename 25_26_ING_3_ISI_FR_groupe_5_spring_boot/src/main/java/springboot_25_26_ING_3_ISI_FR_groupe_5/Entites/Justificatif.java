package springboot_25_26_ING_3_ISI_FR_groupe_5.Entites;

import jakarta.persistence.*;
import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeJustificatif;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Justificatif {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contenu;
    private StatutJustificatif status;
    private TypeJustificatif  justificatif ;
    @ManyToOne
    private AssistantPedagogique assistantPedagogique;
    @OneToMany(mappedBy = "justificatifs")
    private Collection<SeanceCours> seance;
    @ManyToMany(mappedBy = "justificatifs")
    private  Collection<Fichier> fichier= new ArrayList<>();
}