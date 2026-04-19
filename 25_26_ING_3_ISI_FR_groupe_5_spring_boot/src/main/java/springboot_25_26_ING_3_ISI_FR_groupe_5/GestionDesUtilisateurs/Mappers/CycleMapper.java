package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Enums.TypeCycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    @Mapping(target = "libelle", source = "typeCycle", qualifiedByName = "getLibelleFromType")
    @Mapping(target = "nombreFilieres", source = "filieres", qualifiedByName = "countFilieres")
    CycleResponse toResponse(Cycle cycle);

    List<CycleResponse> toResponseList(List<Cycle> cycles);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "filieres", ignore = true)
    Cycle toEntity(CycleRequest request);

    @Named("getLibelleFromType")
    default String getLibelleFromType(TypeCycle typeCycle) {
        if (typeCycle == null) return null;
        return typeCycle.getLibelle();
    }

    @Named("countFilieres")
    default int countFilieres(java.util.Collection<?> filieres) {
        return filieres != null ? filieres.size() : 0;
    }
}