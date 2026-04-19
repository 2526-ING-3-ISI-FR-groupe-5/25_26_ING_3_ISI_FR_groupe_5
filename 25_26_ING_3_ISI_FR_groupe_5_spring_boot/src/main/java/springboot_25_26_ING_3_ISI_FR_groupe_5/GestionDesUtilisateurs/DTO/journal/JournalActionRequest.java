package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.StatutAction;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalActionRequest {

    private Long utilisateurId;

    private TypeAction typeAction;

    private String entiteConcernee;

    private Long entiteId;

    private String description;

    private StatutAction statut;
}