package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationResponse {

    private TypeMigration typeMigration;
    private Long institutId;
    private String institutNom;

    // Compteurs
    private int totalTraite;
    private int totalAdmis;
    private int totalRedoublants;
    private int totalExclus;
    private int totalDiplomes;
    private int totalIgnores;

    // Listes détaillées
    private List<String> admis;
    private List<String> redoublants;
    private List<String> exclus;
    private List<String> diplomes;
    private List<String> ignores;

    private String message;
    private LocalDateTime dateMigration;

    // Utilitaire pour le template Thymeleaf
    public boolean isSimulation() {
        return typeMigration == TypeMigration.SIMULATION;
    }
}