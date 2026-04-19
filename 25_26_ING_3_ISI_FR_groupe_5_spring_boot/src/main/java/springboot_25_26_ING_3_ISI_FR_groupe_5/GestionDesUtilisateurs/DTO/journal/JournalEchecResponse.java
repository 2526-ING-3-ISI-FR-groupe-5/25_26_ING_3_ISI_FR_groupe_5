package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.journal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEchecResponse {

    private Long utilisateurId;
    private String utilisateurNom;
    private String utilisateurPrenom;
    private Long nombreEchecs;
}