package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.programmation;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.enseignant.EnseignantResponse;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgrammationResponse {

    private Long id;
    private Long ueId;
    private String ueNom;
    private String ueCode;
    private Long semestreId;
    private String semestreNom;
    private Long classeId;
    private String classeNom;
    private Long dheure;
    private Long nbrCredit;
    private List<EnseignantResponse> enseignants;
}
