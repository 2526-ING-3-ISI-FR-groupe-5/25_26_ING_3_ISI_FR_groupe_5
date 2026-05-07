package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.UE;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ue.UERequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ue.UEResponse;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Specialite;

@Mapper(componentModel = "spring")
public interface UEMapper {

    @Mapping(target = "specialiteId", source = "specialite.id")
    @Mapping(target = "specialiteNom", source = "specialite.nom")
    @Mapping(target = "specialiteCode", source = "specialite.code")
    @Mapping(target = "nombreProgrammations", source = "programmations", qualifiedByName = "countProgrammations")
    UEResponse toResponse(UE ue);

    List<UEResponse> toResponseList(List<UE> ues);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "programmations", ignore = true)
    UE toEntity(UERequest request);

    @Named("countProgrammations")
    default int countProgrammations(java.util.Set<?> programmations) {
        return programmations != null ? programmations.size() : 0;
    }
}