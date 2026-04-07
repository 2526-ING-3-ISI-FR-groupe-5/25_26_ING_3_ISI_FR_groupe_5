package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationResponse {

    private int totalTraite;
    private List<String> admis;
    private List<String> redoublants;
    private List<String> exclus;
    private List<String> diplomes;
    private List<String> ignores;
    private String message;
}

