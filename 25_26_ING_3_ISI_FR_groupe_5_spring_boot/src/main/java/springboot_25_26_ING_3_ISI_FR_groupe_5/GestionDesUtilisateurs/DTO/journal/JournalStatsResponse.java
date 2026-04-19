package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalStatsResponse {

    private TypeAction typeAction;
    private String typeActionLibelle;
    private Long nombre;
}