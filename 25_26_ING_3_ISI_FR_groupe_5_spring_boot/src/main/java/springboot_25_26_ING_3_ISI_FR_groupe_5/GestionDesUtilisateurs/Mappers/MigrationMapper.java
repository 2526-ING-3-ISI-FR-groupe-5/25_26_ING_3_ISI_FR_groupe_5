package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Migration.MigrationResponse;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeMigration;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Services.ServiceImple.MigrationResultat;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring",
        imports = {LocalDateTime.class},
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MigrationMapper {

    @Mapping(target = "typeMigration", source = "typeMigration")
    @Mapping(target = "totalTraite", expression = "java(resultat.getTotal())")
    @Mapping(target = "totalAdmis", expression = "java(resultat.getAdmis().size())")
    @Mapping(target = "totalRedoublants", expression = "java(resultat.getRedoublants().size())")
    @Mapping(target = "totalExclus", expression = "java(resultat.getExclus().size())")
    @Mapping(target = "totalDiplomes", expression = "java(resultat.getDiplomes().size())")
    @Mapping(target = "totalIgnores", expression = "java(resultat.getIgnores().size())")
    @Mapping(target = "admis", source = "resultat.admis")
    @Mapping(target = "redoublants", source = "resultat.redoublants")
    @Mapping(target = "exclus", source = "resultat.exclus")
    @Mapping(target = "diplomes", source = "resultat.diplomes")
    @Mapping(target = "ignores", source = "resultat.ignores")
    @Mapping(target = "message", expression = "java(resultat.toString())")
    @Mapping(target = "dateMigration", expression = "java(LocalDateTime.now())")
    MigrationResponse toMigrationResponse(MigrationResultat resultat, TypeMigration typeMigration);
}