package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration;

import lombok.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Services.ServiceImple.MigrationResultat;

import java.time.LocalDateTime;
import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MigrationResponse {

    private TypeMigration typeMigration;

    // 🆕 Informations sur l'institut migré
    private Long institutId;
    private String institutNom;

    private int totalTraite;
    private int totalAdmis;
    private int totalRedoublants;
    private int totalExclus;
    private int totalDiplomes;
    private int totalIgnores;

    private List<String> admis;
    private List<String> redoublants;
    private List<String> exclus;
    private List<String> diplomes;
    private List<String> ignores;

    private String message;
    private LocalDateTime dateMigration;

    public static MigrationResponse fromResultat(
            MigrationResultat resultat,
            TypeMigration typeMigration,
            boolean simulation) {

        return MigrationResponse.builder()
                .typeMigration(typeMigration)
                .totalTraite(resultat.getTotal())
                .totalAdmis(resultat.getAdmis().size())
                .totalRedoublants(resultat.getRedoublants().size())
                .totalExclus(resultat.getExclus().size())
                .totalDiplomes(resultat.getDiplomes().size())
                .totalIgnores(resultat.getIgnores().size())
                .admis(resultat.getAdmis())
                .redoublants(resultat.getRedoublants())
                .exclus(resultat.getExclus())
                .diplomes(resultat.getDiplomes())
                .ignores(resultat.getIgnores())
                .message(resultat.toString())
                .dateMigration(LocalDateTime.now())
                .build();
    }

    // Méthode de convenance (sans simulation)
    public static MigrationResponse fromResultat(
            MigrationResultat resultat,
            TypeMigration typeMigration) {
        return fromResultat(resultat, typeMigration, false);
    }
}