package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;


import org.mapstruct.*;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Cycle;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.Cycle.CycleResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CycleMapper {

    CycleResponse toResponse(Cycle cycle);

    List<CycleResponse> toResponseList(List<Cycle> cycles);

    @BeanMapping(builder = @Builder(disableBuilder = true))
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createAt", ignore = true)
    @Mapping(target = "updateAt", ignore = true)
    @Mapping(target = "filieres", ignore = true)
    Cycle toEntity(CycleRequest request);
}