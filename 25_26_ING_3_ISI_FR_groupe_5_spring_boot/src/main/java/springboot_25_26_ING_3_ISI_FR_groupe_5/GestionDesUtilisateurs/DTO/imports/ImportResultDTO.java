package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.imports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    private int totalLignes;
    private int lignesImportees;
    private int lignesIgnorees;
    private int lignesErreurs;
    @Builder.Default
    private List<String> erreurs = new ArrayList<>();
}