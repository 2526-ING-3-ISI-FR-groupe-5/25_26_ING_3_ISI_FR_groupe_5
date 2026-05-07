package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Filiere;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.filiere.FiliereRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.filiere.FiliereResponse;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Enum.TypeCycle;

@Mapper(componentModel = "spring")
public interface FiliereMapper {

    @Mapping(target = "ecoleId", source = "ecole.id")
    @Mapping(target = "ecoleNom", source = "ecole.nom")
    @Mapping(target = "cycleId", source = "cycle.id")
    @Mapping(target = "cycleNom", source = "cycle.typeCycle")
    @Mapping(target = "cycleLibelle", source = "cycle.typeCycle.libelle")
    FiliereResponse toResponse(Filiere filiere);

    List<FiliereResponse> toResponseList(List<Filiere> filieres);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "ecole", ignore = true)
    @Mapping(target = "cycle", ignore = true)
    @Mapping(target = "specialites", ignore = true)
    @Mapping(target = "niveaux", ignore = true)
    Filiere toEntity(FiliereRequest request);
}