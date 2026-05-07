package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Cycle;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CycleResponse {

    private Long id;
    private TypeCycle typeCycle;
    private String libelle;
    private int nombreFilieres;
}