package springboot_25_26_ING_3_ISI_FR_groupe_5.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.StatutJustificatif;
import springboot_25_26_ING_3_ISI_FR_groupe_5.enums.TypeJustificatif;
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
}