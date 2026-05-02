package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Classe;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classes.ClassesRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.classes.ClassesResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClassesMapper {

    @Mapping(target = "niveauId", source = "niveau.id")
    @Mapping(target = "niveauNom", source = "niveau.nom")
    @Mapping(target = "niveauOrdre", source = "niveau.ordre")
    @Mapping(target = "specialiteId", source = "niveau.specialite.id")
    @Mapping(target = "specialiteNom", source = "niveau.specialite.nom")
    @Mapping(target = "specialiteCode", source = "niveau.specialite.code")
    @Mapping(target = "filiereId", source = "niveau.specialite.filiere.id")
    @Mapping(target = "filiereNom", source = "niveau.specialite.filiere.nom")
    @Mapping(target = "cycleId", source = "niveau.specialite.filiere.cycle.id")
    @Mapping(target = "cycleNom", source = "niveau.specialite.filiere.cycle.typeCycle.libelle")
    @Mapping(target = "ecoleId", source = "niveau.specialite.filiere.ecole.id")
    @Mapping(target = "ecoleNom", source = "niveau.specialite.filiere.ecole.nom")
    @Mapping(target = "institutId", source = "niveau.specialite.filiere.ecole.institut.id")
    @Mapping(target = "institutNom", source = "niveau.specialite.filiere.ecole.institut.nom")
    @Mapping(target = "nombreEtudiants", source = "inscriptions", qualifiedByName = "countInscriptions")
    @Mapping(target = "nombreProgrammations", source = "programmations", qualifiedByName = "countProgrammations")
    ClassesResponse toResponse(Classe classe);

    List<ClassesResponse> toResponseList(List<Classe> classes);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "niveau", ignore = true)
    @Mapping(target = "plagesHoraires", ignore = true)
    @Mapping(target = "inscriptions", ignore = true)
    @Mapping(target = "programmations", ignore = true)
    Classe toEntity(ClassesRequest request);

    @Named("countInscriptions")
    default int countInscriptions(java.util.Set<?> inscriptions) {
        return inscriptions != null ? inscriptions.size() : 0;
    }

    @Named("countProgrammations")
    default int countProgrammations(java.util.Set<?> programmations) {
        return programmations != null ? programmations.size() : 0;
    }
}