package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.Migration.MigrationResultat;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Enum.TypeMigration;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MigrationMapper {

    /**
     * Mappe MigrationResultat → MigrationResponse
     *
     * ✅ Les listes (admis, redoublants...) sont mappées AUTOMATIQUEMENT
     * car les noms de propriétés sont identiques.
     *
     * ✅ Les compteurs utilisent `expression = "java(...)"` pour accéder
     * aux getters de l'objet `resultat`.
     */
    @Mapping(target = "typeMigration", source = "typeMigration")
    @Mapping(target = "institutId", source = "institutId")
    @Mapping(target = "institutNom", source = "institutNom")

    // ✅ Compteurs : utiliser `expression` pour accéder aux getters de `resultat`
    @Mapping(target = "totalAdmis", expression = "java(resultat.getAdmisCount())")
    @Mapping(target = "totalRedoublants", expression = "java(resultat.getRedoublantsCount())")
    @Mapping(target = "totalExclus", expression = "java(resultat.getExclusCount())")
    @Mapping(target = "totalDiplomes", expression = "java(resultat.getDiplomesCount())")
    @Mapping(target = "totalIgnores", expression = "java(resultat.getIgnoresCount())")

    // ✅ Total calculé
    @Mapping(target = "totalTraite", expression = "java(resultat.getTotalTraite())")

    // ✅ Métadonnées
    @Mapping(target = "message", expression = "java(resultat.toString())")
    @Mapping(target = "dateMigration", expression = "java(LocalDateTime.now())")

    // ❌ NE PAS AJOUTER de @Mapping pour les listes : elles sont mappées automatiquement !
    // MapStruct fera : response.setAdmis(new ArrayList<>(resultat.getAdmis()));

    MigrationResponse toMigrationResponse(
            MigrationResultat resultat,
            TypeMigration typeMigration,
            Long institutId,
            String institutNom
    );

    /** Méthode de convenance sans informations d'institut */
    default MigrationResponse toMigrationResponse(MigrationResultat resultat, TypeMigration typeMigration) {
        return toMigrationResponse(resultat, typeMigration, null, null);
    }
}