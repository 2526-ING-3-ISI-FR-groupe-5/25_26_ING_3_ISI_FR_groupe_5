package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Ecole;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ecole.EcoleRequest;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.DTO.ecole.EcoleResponse;

import java.util.List;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionAcademique.Entity.Institut;

@Mapper(componentModel = "spring")
public interface EcoleMapper {

    @Mapping(target = "institutId", source = "institut.id")
    @Mapping(target = "institutNom", source = "institut.nom")
    EcoleResponse toResponse(Ecole ecole);

    List<EcoleResponse> toResponseList(List<Ecole> ecoles);

    @Mapping(target = "id", ignore = true)

    @Mapping(target = "institut", ignore = true)
    @Mapping(target = "filieres", ignore = true)
    Ecole toEntity(EcoleRequest request);
}