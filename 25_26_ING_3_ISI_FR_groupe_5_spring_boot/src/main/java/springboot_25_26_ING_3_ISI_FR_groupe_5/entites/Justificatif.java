package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeJustificatif;

import java.util.Collection;

@Data
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
    @ManyToMany(mappedBy = "justificatifs")
    private Collection<Fichier> fichiers;
    @OneToMany(mappedBy = "justificatif")
    private Collection<SeanceCours> seancecourses;
    @ManyToOne
    private AssistantPedagogique assistantPedagogique;
}