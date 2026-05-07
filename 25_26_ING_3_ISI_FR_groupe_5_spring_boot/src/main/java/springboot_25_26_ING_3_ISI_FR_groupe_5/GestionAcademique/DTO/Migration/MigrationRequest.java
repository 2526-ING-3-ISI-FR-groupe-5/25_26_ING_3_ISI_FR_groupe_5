package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Getter
@Setter
@Builder
public class MigrationRequest {

    @NotNull(message = "L'année cible est obligatoire")
    private Long nouvelleAnneeId;

    // 🆕 Pour le Super Admin : choisir l'institut à migrer
    // Pour les autres rôles : sera forcé par le service
    private Long institutId;

    @Builder.Default
    private TypeMigration typeMigration = TypeMigration.COMPLETE;

    private Long etudiantId;
    private Long enseignantId;
    private Long ueId;
    private Long classeId;
    private Long filiereId;
    private Long niveauId;

    public boolean isSimulation() {
        return typeMigration == TypeMigration.COMPLETE;
    }
}