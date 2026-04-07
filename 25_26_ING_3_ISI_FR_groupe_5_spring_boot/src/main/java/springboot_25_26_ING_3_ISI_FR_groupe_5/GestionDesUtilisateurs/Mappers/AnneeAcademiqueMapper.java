package springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.Mappers;


import CarnetRouge.CarnetRouge.GDU.dtos.annee.AnneeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import springboot_25_26_ING_3_ISI_FR_groupe_5.Entity.Annee_academique;
import springboot_25_26_ING_3_ISI_FR_groupe_5.GestionDesUtilisateurs.DTO.annee.AnneeRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnneeAcademiqueMapper {

    AnneeResponse toResponse(Annee_academique annee);

    List<AnneeResponse> toResponseList(List<Annee_academique> annees);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "semestres", ignore = true)
    Annee_academique toEntity(AnneeRequest request);
}